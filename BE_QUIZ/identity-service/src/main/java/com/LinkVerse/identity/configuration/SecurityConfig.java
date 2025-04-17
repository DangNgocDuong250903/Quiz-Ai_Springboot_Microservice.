package com.LinkVerse.identity.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/users/registration",
        "/auth/token",
        "/auth/introspect",
        "/auth/logout",
        "/auth/refresh",
        "/internal/users",
        "/internal/users/**",
        "/profile/users",
        "/groups/all",
        "/notification/2fa/generate",
        "/notification/2fa/validate",
        "/notification/user/email",
        "/notification/email/user/email",
        "/notification/email/user",
        "/ws/**",
        "/app.ws/**",
        "/app/**",
        "/auth/google",
        "/auth/google/callback",
        "/google/callback",
        "auth/login", // Thêm endpoint callback để xử lý đăng nhập Google
        "/notification/email/send-login-notification",
        "/Ai/gemini/ask",
        "/gemini/ask"
    };

    private final CustomJwtDecoder customJwtDecoder;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder) {
        this.customJwtDecoder = customJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // Cấu hình các endpoint công khai và yêu cầu xác thực
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS)
                        .permitAll()
                        .requestMatchers("/", "/login", "/oauth2/authorization/**")
                        .permitAll() // Cho phép truy cập trang login và OAuth2 authorization
                        .anyRequest()
                        .authenticated())
                // Cấu hình OAuth2 Login cho Google
                .oauth2Login(oauth2 -> oauth2.loginPage("/login") // Trang login tùy chỉnh (nếu có)
                        .defaultSuccessUrl(
                                "/api/auth/google/callback", true) // Chuyển hướng sau khi đăng nhập thành công
                        .failureUrl("/login?error") // Chuyển hướng nếu thất bại
                        .userInfoEndpoint(
                                userInfo -> userInfo.oidcUserService(
                                        oidcUserService()) // Xử lý thông tin người dùng từ Google
                                ))
                // Cấu hình OAuth2 Resource Server để xử lý JWT
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()))
                // Tắt CSRF và CORS (nếu đã xử lý riêng)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.disable());

        // Thêm filter CORS tùy chỉnh

        return httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        jwtAuthenticationConverter.setPrincipalClaimName("userId"); // Sử dụng userId làm principal

        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }

    //    @Bean
    //    public OncePerRequestFilter coopFilter() {
    //        return new CoopConfig().coopFilter();
    //    }
}
