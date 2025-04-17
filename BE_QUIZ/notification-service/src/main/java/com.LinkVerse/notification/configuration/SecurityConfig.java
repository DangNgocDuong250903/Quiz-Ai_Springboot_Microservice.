package com.LinkVerse.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.LinkVerse.notification.service.TokenService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/email/send",
        "email/bill",
        "bill",
        "email/user/id",
        "/user/id",
        "/email/send-forget-pass",
        "/email/reset-password",
        "/email/send-verification",
        "/api/v1/notification/**",
        "/v3/.*",
        "/ws",
        "/ws/**",
        "/2fa/generate",
        "/2fa/validate",
        "/email/user/email",
        "/email/user",
        "/email/send-login-notification"
    };

    private final CustomJwtDecoder customJwtDecoder;
    private final TokenService tokenService;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder, TokenService tokenService) {
        this.customJwtDecoder = customJwtDecoder;
        this.tokenService = tokenService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // Cấu hình quyền truy cập
        httpSecurity.authorizeHttpRequests(request -> request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS)
                .permitAll()
                .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS)
                .permitAll()
                .anyRequest()
                .authenticated());

        // Cấu hình OAuth2 resource server với JWT
        httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        // Vô hiệu hóa CSRF cho API
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        // Thêm JwtRequestFilter trước UsernamePasswordAuthenticationFilter

        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity
                .ignoring()
                .requestMatchers(
                        "/actuator/**",
                        "/v3/**",
                        "/swagger-ui*/**",
                        "/webjars/**",
                        "/swagger-ui*/*swagger-initializer.js",
                        "/swagger-ui*/**");
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}
