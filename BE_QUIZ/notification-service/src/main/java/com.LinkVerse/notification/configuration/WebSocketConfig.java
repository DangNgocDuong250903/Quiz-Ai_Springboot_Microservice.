package com.LinkVerse.notification.configuration;

import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.LinkVerse.notification.service.TokenService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenService tokenService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .addInterceptors(new HttpSessionHandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            ServerHttpRequest request,
                            ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes)
                            throws Exception {
                        // Extract token from the query parameter
                        String query = request.getURI().getQuery();
                        if (query != null && query.startsWith("token=")) {
                            String token = query.split("=", 2)[1]; // Get token value
                            try {
                                // Verify the token
                                tokenService.verifyToken(token, false);
                                // Store token in attributes for future use
                                attributes.put("token", token);
                                return super.beforeHandshake(request, response, wsHandler, attributes);
                            } catch (Exception e) {
                                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                                return false;
                            }
                        }
                        response.setStatusCode(HttpStatus.BAD_REQUEST);
                        return false;
                    }
                })
                .withSockJS();
    }
}
