package com.LinkVerse.identity.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.LinkVerse.event.dto.NotificationEvent;
import com.LinkVerse.identity.constant.PredefinedRole;
import com.LinkVerse.identity.dto.request.ProfileUpdateRequest;
import com.LinkVerse.identity.dto.request.UserCreationRequest;
import com.LinkVerse.identity.dto.request.UserUpdateRequest;
import com.LinkVerse.identity.dto.request.UserUpdateRequestAdmin;
import com.LinkVerse.identity.dto.response.UserResponse;
import com.LinkVerse.identity.entity.Role;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.entity.UserStatus;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.exception.ErrorCode;
import com.LinkVerse.identity.mapper.UserMapper;
import com.LinkVerse.identity.repository.RoleRepository;
import com.LinkVerse.identity.repository.UserRepository;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    KafkaTemplate<String, Object> kafkaTemplate;
    NotificationService notificationService;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    //    public void processOAuthPostLogin(CustomOAuth2User oauthUser) {
    //        String email = oauthUser.getEmail();
    //        Optional<User> optionalUser = userRepository.findByEmail(email);
    //
    //        if (optionalUser.isEmpty()) {
    //            User newUser = new User();
    //            newUser.setEmail(email);
    //            newUser.setFirstName(oauthUser.getAttribute("given_name"));
    //            newUser.setLastName(oauthUser.getAttribute("family_name"));
    //            newUser.setImageUrl(oauthUser.getAttribute("picture"));
    //            newUser.setUsername(oauthUser.getName());
    //            newUser.setEmailVerified(true);
    //            newUser.setStatus(UserStatus.ONLINE);
    //            newUser.setCreatedAt(LocalDateTime.now());
    //            userRepository.save(newUser);
    //        } else {
    //            User existingUser = optionalUser.get();
    //            existingUser.setFirstName(oauthUser.getAttribute("given_name"));
    //            existingUser.setLastName(oauthUser.getAttribute("family_name"));
    //            existingUser.setImageUrl(oauthUser.getAttribute("picture"));
    //            existingUser.setUsername(oauthUser.getName());
    //            userRepository.save(existingUser);
    //        }
    //    }

    public void updateImage(String userId, String imageUrl) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        user.setImageUrl(imageUrl);
        userRepository.save(user);
    }

    public UserResponse createUser(UserCreationRequest request) {
    // Validate phone number
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().matches("\\d{10}")) {
        log.error("Invalid phone number: {}", request.getPhoneNumber());
        throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
    }

    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
        long phoneNumberUsageCount = userRepository.countByPhoneNumber(request.getPhoneNumber());
        if (phoneNumberUsageCount >= 3) {
            log.error("Phone number usage limit exceeded: {}", request.getPhoneNumber());
            throw new AppException(ErrorCode.PHONE_NUMBER_MAX_USAGE_EXCEEDED);
        }
    }

    // ✅ Validate MSSV duy nhất
    if (request.getMssv() != null && userRepository.existsByMssv(request.getMssv())) {
        log.error("MSSV already exists: {}", request.getMssv());
        throw new AppException(ErrorCode.MSSV_ALREADY_EXISTS);
    }

    User user = userMapper.toUser(request);
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setMssv(request.getMssv()); // Gán MSSV

    HashSet<Role> roles = new HashSet<>();
    roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
    user.setRoles(roles);
    user.setEmailVerified(false);

    if (request.getStatus() == null) {
        user.setStatus(UserStatus.ONLINE);
    } else {
        user.setStatus(request.getStatus());
    }

    user.setGender(request.getGender());

    try {
        user = userRepository.save(user);
    } catch (DataIntegrityViolationException exception) {
        throw new AppException(ErrorCode.USER_EXISTED);
    }

    // Gửi thông báo
    NotificationEvent notificationEvent = NotificationEvent.builder()
            .channel("EMAIL")
            .recipient(request.getEmail())
            .subject("Welcome to LinkVerse")
            .body("Hello, " + request.getUsername())
            .build();

    kafkaTemplate.send("notification-delivery", notificationEvent);

    List<String> userTokens = getAllUserTokensExcept(user);
    notificationService.sendNotificationToUsers(
            userTokens, "New User Registered", "A new user has joined: " + user.getUsername());

    return userMapper.toUserResponse(user);
}


    private List<String> getAllUserTokensExcept(User newUser) {
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(newUser.getId()))
                .map(User::getFcmToken)
                .collect(Collectors.toList());
    }

    public UserResponse updateStatus(String userId, String status) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setStatus(UserStatus.valueOf(status));
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            log.error("Security context or authentication is null");
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        String name = context.getAuthentication().getName();
        log.info("Authenticated user: {}", name);

        User user = userRepository.findByUsername(name).orElseThrow(() -> {
            log.error("User not found: {}", name);
            return new AppException(ErrorCode.USER_NOT_EXISTED);
        });

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(String userId, UserUpdateRequestAdmin request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    //    public UserResponse updateUserbyUsers(String userId, UserUpdateRequest request) {
    //        User user = userRepository.findById(userId).orElseThrow(() -> new
    // AppException(ErrorCode.USER_NOT_EXISTED));
    //
    //        if (request.getFirstName() != null) {
    //            user.setFirstName(request.getFirstName());
    //        }
    //        if (request.getLastName() != null) {
    //            user.setLastName(request.getLastName());
    //        }
    //        if (request.getEmail() != null) {
    //            if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
    //                throw new AppException(ErrorCode.INVALID_EMAIL);
    //            }
    //            user.setEmail(request.getEmail());
    //        }
    //        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
    //            if (!PHONE_PATTERN.matcher(request.getPhoneNumber()).matches()) {
    //                throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
    //            }
    //            user.setPhoneNumber(request.getPhoneNumber());
    //        }
    //        if (request.getDateOfBirth() != null) {
    //            user.setDateOfBirth(request.getDateOfBirth());
    //        }
    //        if (request.getCity() != null) {
    //            user.setCity(request.getCity());
    //        }
    //        user.setBio(request.getBio());
    //
    //        if (request.getUsername() != null) {
    //            user.setUsername(request.getUsername());
    //        }
    //        return userMapper.toUserResponse(userRepository.save(user));
    //    }
    public UserResponse updateUserbyUsers(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
                throw new AppException(ErrorCode.INVALID_EMAIL);
            }
            if (userRepository.existsByEmail(request.getEmail())
                    && !user.getEmail().equals(request.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getUsername() != null) {
            if (userRepository.existsByUsername(request.getUsername())
                    && !user.getUsername().equals(request.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_ALREADY_EXISTS);
            }
            user.setUsername(request.getUsername());
        }

        log.info("UserId thong bao ra man hinh: {}", userId);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    // xoá tạm thời sau 30 ngày nếu không đăng nhập -> xoá vĩnh viễn
    public void deleteUser(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();

        User user = userRepository.findUserById(userID);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(password, user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        user.setDeletedAt(LocalDateTime.now()); // đã có service xử lý xoá vĩnh viễn sau 30 ngày
        // nếu login lại trước 30 ngày thì xoá deletedAt -> authentication service
        userRepository.save(user);
    }

    // xoá vĩnh viễn trong vòng 30 ngày
    public void deleteUserPermanent(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();

        User user = userRepository.findUserById(userID);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(password, user.getPassword());
        if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);


            userRepository.delete(user);

    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserByAdmin(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.delete(user);
    }

    public Page<UserResponse> getUsers(int page, int size) {
        log.info("In method get Users");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        List<UserResponse> userResponses =
                userPage.getContent().stream().map(userMapper::toUserResponse).toList();
        return new PageImpl<>(userResponses, pageable, userPage.getTotalElements());
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
    }

    public User findUserById(String id) {
        return this.userRepository.findUserById(id);
    }
}
