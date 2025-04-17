package com.LinkVerse.gateway.configuration;

import com.LinkVerse.gateway.dto.ApiResponse;
import com.LinkVerse.gateway.service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class AuthenticationFilter implements GlobalFilter, Ordered {
    IdentityService identityService;
    ObjectMapper objectMapper;

    @NonFinal
    private String[] publicEndpoints = {
            "/identity/auth/.*",
            "/identity/users/registration",
            "/notification/email/send",
            "/notification/email/forgot-password",
            "/email/reset-password",
            "/email/send-forget-pass",
            "/email/reset-password",
            "/search", "/sorts", "/criteria-search", "/advanced-search",
            "/users/random",
            "/identity/users/random",
            "/profile/users/random",
            "/post/all", "/all",
            "/groups/all", "/identity/groups/all",
            "/ws/.*",
            "/identity/ws/.*",
            "/identity/chats/.*",
            "/identity/messages/.*",
            "/users/registration", "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh",
            "/internal/users", "/internal/users/.*", "/internal/roles", "/internal/roles/.*", "/posts-random", "/post/posts-random",
            "notification/2fa/generate", "notification/2fa/validate","email/user/id","notification/email/user/id",
            "Ai/AiTele/ask" ,"AiTele/ask",
            "/donation/telegram/webhook","/telegram/webhook",
            "notification/email/user/email",
            "notification/email/user",
            "notification/email/bill",
            "notification/email/bill",
            "/profile/users", "/post/posts-random",
            "/donation/payments/vnpay_ipn",
            "/donation/payments/.*",
            "/donation/payments/.*/vn-pay-callback",
            "/donation/payments/[a-fA-F0-9\\-]+/vn-pay-callback", "/gemini/ask", "Ai/gemini/ask", "/Ai/gemini/ask"
    };

    @Value("${app.api-prefix}")
    @NonFinal
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Enter authentication filter....");

        if (isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if (CollectionUtils.isEmpty(authHeader))
            return unauthenticated(exchange.getResponse());

        String token = authHeader.getFirst().replace("Bearer ", "");
        log.info("Token: {}", token);

        return identityService.introspect(token).flatMap(introspectResponse -> {
            if (introspectResponse.getResult().isValid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request) {
        log.info("Checking public endpoint: {}", request.getURI().getPath());
        return Arrays.stream(publicEndpoints)
                .anyMatch(s -> request.getURI().getPath().matches(apiPrefix + s));
    }


    Mono<Void> unauthenticated(ServerHttpResponse response) {
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();

        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}