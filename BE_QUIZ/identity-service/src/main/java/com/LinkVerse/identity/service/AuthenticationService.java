package com.LinkVerse.identity.service;

import com.LinkVerse.identity.dto.request.*;
import com.LinkVerse.identity.dto.response.AuthenticationResponse;
import com.LinkVerse.identity.dto.response.IntrospectResponse;
import com.LinkVerse.identity.entity.DeviceInfo;
import com.LinkVerse.identity.entity.InvalidatedToken;
import com.LinkVerse.identity.entity.LoginHistory;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.exception.ErrorCode;
import com.LinkVerse.identity.repository.InvalidatedTokenRepository;
import com.LinkVerse.identity.repository.LoginHistoryRepository;
import com.LinkVerse.identity.repository.UserRepository;
import com.LinkVerse.identity.repository.httpclient.NotificationClient;
import com.LinkVerse.identity.util.DeviceInfoUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    NotificationClient notificationClient;
    LoginHistoryRepository loginHistoryRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    public void changePassword(String requesterId, PasswordChangeRequest request, HttpServletRequest httpRequest)
            throws ParseException, JOSEException {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        User user =
                userRepository.findById(requesterId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // So sánh mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        // Không cho trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.NEW_PASSWORD_MUST_DIFFERENT);
        }

        // Kiểm tra confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Vô hiệu hóa token hiện tại
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            SignedJWT signedJWT = verifyToken(token);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiry = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiry).build();
            invalidatedTokenRepository.save(invalidatedToken);
        }
    }

    public record TokenInfo(String token, Date expiryDate) {
        public String getToken() {
            return token;
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        User user;
        if ("admin".equalsIgnoreCase(request.getUsername())) {
            // Skip email verification if the username is "admin"
            user = userRepository
                    .findByUsername("admin")
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        } else if (request.getUsername().contains("@")) {
            try {
                ResponseEntity<User> userResponse = notificationClient.getUserByEmail(request.getUsername());
                if (!userResponse.getStatusCode().is2xxSuccessful() || userResponse.getBody() == null) {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                }
                user = userResponse.getBody();
            } catch (FeignException.NotFound e) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }
        } else {
            try {
                ResponseEntity<String> emailResponse = notificationClient.getUserEmail(request.getUsername());
                if (!emailResponse.getStatusCode().is2xxSuccessful() || emailResponse.getBody() == null) {
                    throw new AppException(ErrorCode.EMAIL_NOT_FOUND);
                }
                String userEmail = emailResponse.getBody();
                user = userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            } catch (FeignException.NotFound e) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Skip OTP verification if the username is "admin"
        if (!"admin".equalsIgnoreCase(request.getUsername())) {
            if (request.getOtp() == 0) {
                try {
                    ResponseEntity<String> response = notificationClient.generateOtp(user.getEmail());
                    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                        throw new AppException(ErrorCode.OTP_GENERATION_FAILED);
                    }
                    throw new AppException(ErrorCode.OTP_SENT);
                } catch (FeignException.TooManyRequests e) {
                    throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
                } catch (FeignException.InternalServerError e) {
                    throw new AppException(ErrorCode.WAIT_BEFORE_RETRY);
                }
            }
            try {
                ResponseEntity<String> otpResponse = notificationClient.validateOtp(user.getEmail(), request.getOtp());
                if (!otpResponse.getStatusCode().is2xxSuccessful()) {
                    throw new AppException(ErrorCode.INVALID_OTP);
                }
            } catch (FeignException.BadRequest e) {
                throw new AppException(ErrorCode.INVALID_OTP);
            }
        }
        DeviceInfo deviceInfo = DeviceInfoUtil.extractDeviceInfo(httpRequest);
        List<DeviceInfo> knownDevices =
                user.getKnownDevices() != null ? new ArrayList<>(user.getKnownDevices()) : new ArrayList<>();
        if (!knownDevices.contains(deviceInfo)) {
            knownDevices.add(deviceInfo);
            user.setKnownDevices(knownDevices);
        }

        LoginHistory loginHistory =
                LoginHistory.builder().user(user).deviceInfo(deviceInfo).build();
        loginHistoryRepository.save(loginHistory);

        // Gửi thông báo đăng nhập nếu không phải admin
        if (!"admin".equalsIgnoreCase(request.getUsername())) {
            try {
                ResponseEntity<Void> response = notificationClient.sendLoginNotification(user);
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new AppException(ErrorCode.LOGIN_NOTIFICATION_FAILED);
                }
            } catch (FeignException.TooManyRequests e) {
                throw new AppException(ErrorCode.TOO_MANY_REQUESTS);
            } catch (FeignException.InternalServerError e) {
                throw new AppException(ErrorCode.WAIT_BEFORE_RETRY);
            } catch (FeignException e) {
                // Tránh lỗi 9999 hoặc lỗi bất ngờ làm crash
                log.warn("Send login notification failed: {}", e.getMessage());
            }
        }
        //        notificationClient.sendLoginNotification(user);

        //        DeviceInfo deviceInfo = DeviceInfoUtil.extractDeviceInfo(httpRequest);
        //
        //        List<DeviceInfo> knownDevices = user.getKnownDevices() != null ? new
        // ArrayList<>(user.getKnownDevices()) : new ArrayList<>();
        //        if (!knownDevices.contains(deviceInfo)) {
        //            knownDevices.add(deviceInfo);
        //            user.setKnownDevices(knownDevices);
        //        }
        //
        //        // Lưu lịch sử đăng nhập
        //        LoginHistory loginHistory = LoginHistory.builder()
        //                .user(user)
        //                .deviceInfo(deviceInfo)
        //                .build();
        //        loginHistoryRepository.save(loginHistory);

        if (user.getDeletedAt() != null) {
            user.setDeletedAt(null);
        }
        userRepository.save(user);

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token.token)
                .expiryTime(token.expiryDate)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token.token)
                .expiryTime(token.expiryDate)
                .build();
    }

    private List<String> buildAuthorities(User user) {
        List<String> authorities = new ArrayList<>();

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            for (var role : user.getRoles()) {
                authorities.add("ROLE_" + role.getName());

                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    for (var permission : role.getPermissions()) {
                        authorities.add(permission.getName());
                    }
                }
            }
        }

        return authorities;
    }

    public TokenInfo generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Date issueTime = new Date();
        Date expiryTime = new Date(issueTime.getTime() + validDuration * 1000);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("LinkVerseND.com")
                .issueTime(issueTime)
                .expirationTime(expiryTime)
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .claim("userId", user.getId())
                .claim("ProfileID", user.getProfileId())
                .claim("authorities", buildAuthorities(user)) //  Đây là điểm quan trọng để gắn quyền vào token
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return new TokenInfo(jwsObject.serialize(), expiryTime);
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    public SignedJWT verifyToken(String token) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
