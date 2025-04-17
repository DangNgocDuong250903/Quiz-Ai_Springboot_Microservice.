package com.LinkVerse.identity.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.LinkVerse.identity.service.GoogleLoginService;

@RestController
@RequestMapping("/auth")
public class GoogleLoginController {
    @Autowired
    private GoogleLoginService googleLoginService;

    @PostMapping("/google")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody Map<String, String> request) {
        String accessToken = request.get("access_token");
        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing access token"));
        }

        try {
            Map<String, Object> response = googleLoginService.handleGoogleLogin(accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process Google login: " + e.getMessage()));
        }
    }

    // Test login tren khong anh huong gi
    @GetMapping("/google/callback")
    public ResponseEntity<Map<String, Object>> googleCallback(
            @AuthenticationPrincipal OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication failed"));
        }
        Map<String, Object> response = googleLoginService.handleGoogleLogin(authentication);
        return ResponseEntity.ok(response);
    }
}
