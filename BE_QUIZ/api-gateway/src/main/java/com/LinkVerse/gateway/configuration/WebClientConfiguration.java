package com.LinkVerse.gateway.configuration;

import com.LinkVerse.gateway.repository.IdentityClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.List;

@Configuration
public class WebClientConfiguration {
    @Bean
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:9090/identity")
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:3000")); // Allow requests from localhost:5173
        corsConfiguration.setAllowedHeaders(List.of("*")); // Allow all headers
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow specific HTTP methods
        corsConfiguration.setAllowCredentials(true); // Enable credentials (if cookies or Authorization headers are required)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsWebFilter(source);
    }

    @Bean
    IdentityClient identityClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient)).build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }
}