package com.LinkVerse.identity.controller;

import java.text.ParseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.LinkVerse.identity.dto.request.*;
import com.LinkVerse.identity.dto.response.AuthenticationResponse;
import com.LinkVerse.identity.dto.response.IntrospectResponse;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.repository.httpclient.NotificationClient;
import com.LinkVerse.identity.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    NotificationClient notificationClient;

    //    @PostMapping("/token")
    //    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws
    // Exception {
    //        AuthenticationResponse result = authenticationService.authenticate(request);
    //        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    //    }
    @PostMapping("/token")
    public ApiResponse<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request, HttpServletRequest httpRequest) throws Exception {
        AuthenticationResponse result = authenticationService.authenticate(request, httpRequest);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder().result(result).build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder().build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(
            @RequestBody @Valid PasswordChangeRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String requesterId = authentication.getName();

            authenticationService.changePassword(requesterId, request, httpRequest);

            return ApiResponse.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("Password changed successfully. Please log in again.")
                    .build();

        } catch (AppException ex) {
            return ApiResponse.<Void>builder()
                    .code(ex.getErrorCode().getCode())
                    .message(ex.getErrorCode().getMessage())
                    .build();

        } catch (Exception ex) {
            log.error("Failed to change password", ex);
            return ApiResponse.<Void>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An error occurred while changing password")
                    .build();
        }
    }
}
