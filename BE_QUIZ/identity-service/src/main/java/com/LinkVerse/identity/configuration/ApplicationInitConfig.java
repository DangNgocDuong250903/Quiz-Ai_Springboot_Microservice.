package com.LinkVerse.identity.configuration;

import com.LinkVerse.identity.constant.PredefinedRole;
import com.LinkVerse.identity.entity.Permission;
import com.LinkVerse.identity.entity.Role;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.entity.UserStatus;
import com.LinkVerse.identity.repository.PermissionRepository;
import com.LinkVerse.identity.repository.RoleRepository;
import com.LinkVerse.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    PasswordEncoder passwordEncoder;

    @NonFinal
    static final String ADMIN_USER_NAME = "admin";

    @NonFinal
    static final String ADMIN_PASSWORD = "admin";

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository
    ) {
        log.info("Initializing application.....");

        return args -> {
            // Tạo quyền mặc định
            List<String> defaultPermissions = List.of(
                    "QUIZ_CREATE",
                    "QUIZ_VIEW",
                    "QUIZ_UPDATE",
                    "QUIZ_DELETE",
                    "USER_MANAGE",
                    "USER_VIEW",
                    "CHEATING_ANALYZE"
            );

            for (String perm : defaultPermissions) {
                permissionRepository.findById(perm).orElseGet(() -> permissionRepository.save(
                        Permission.builder().name(perm).description("Permission: " + perm).build()
                ));
            }

            // Gán quyền cho USER_ROLE
            Role userRole = roleRepository.findByName(PredefinedRole.USER_ROLE)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(PredefinedRole.USER_ROLE)
                                    .description("User role")
                                    .build()
                    ));

            List<String> userPermissions = List.of("QUIZ_VIEW", "USER_VIEW");
            userRole.setPermissions(new HashSet<>(permissionRepository.findAllById(userPermissions)));
            roleRepository.save(userRole);

            // Gán toàn bộ quyền cho ADMIN_ROLE
            Role adminRole = roleRepository.findByName(PredefinedRole.ADMIN_ROLE)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(PredefinedRole.ADMIN_ROLE)
                                    .description("Admin role")
                                    .build()
                    ));

            adminRole.setPermissions(new HashSet<>(permissionRepository.findAll()));
            roleRepository.save(adminRole);

            // Tạo tài khoản admin mặc định nếu chưa tồn tại
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(new HashSet<>(List.of(adminRole)))
                        .status(UserStatus.ONLINE)
                        .build();

                userRepository.save(user);
                log.warn("Admin user has been created with default password: admin, please change it.");
            }

            log.info("✅ Application initialization completed.");
        };
    }
}
