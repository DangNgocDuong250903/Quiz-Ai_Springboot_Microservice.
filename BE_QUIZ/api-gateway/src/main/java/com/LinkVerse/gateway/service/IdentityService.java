package com.LinkVerse.gateway.service;

import com.LinkVerse.gateway.dto.ApiResponse;
import com.LinkVerse.gateway.dto.request.IntrospectRequest;
import com.LinkVerse.gateway.dto.response.IntrospectResponse;
import com.LinkVerse.gateway.repository.IdentityClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public Mono<ApiResponse<IntrospectResponse>> introspect(String token) { //nhận token raw từ httpHeader
        return identityClient.introspect(IntrospectRequest.builder() //truyền token raw vào endpoint Introspect từ IS
                .token(token)
                .build());
    }
}
