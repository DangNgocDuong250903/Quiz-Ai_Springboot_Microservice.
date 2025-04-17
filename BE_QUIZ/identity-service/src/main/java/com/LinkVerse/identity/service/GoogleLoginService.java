package com.LinkVerse.identity.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.repository.UserRepository;

@Service
public class GoogleLoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    // Phiên bản cho Implicit Flow (nhận access_token từ Google)
    public Map<String, Object> handleGoogleLogin(String accessToken) {
        Map<String, Object> userInfo = verifyGoogleToken(accessToken);
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String googleId = (String) userInfo.get("sub");

        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setFullName(name);
        if (user.getId() == null) {
            user.setId(googleId);
            user.setUsername(email);
            user.setPassword("");
            user.setProfileId(UUID.randomUUID().toString());
        }
        userRepository.save(user);

        AuthenticationService.TokenInfo tokenInfo = authenticationService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", tokenInfo.token());
        response.put("expiry_time", tokenInfo.expiryDate());
        response.put("email", email);
        response.put("name", name);
        return response;
    }

    // Phiên bản cho Authorization Code Flow (nhận từ Spring Security)
    public Map<String, Object> handleGoogleLogin(OAuth2AuthenticationToken authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", authentication.getPrincipal().getAttribute("email"));
        userInfo.put("name", authentication.getPrincipal().getAttribute("name"));
        userInfo.put("sub", authentication.getPrincipal().getAttribute("sub"));

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String googleId = (String) userInfo.get("sub");

        User user = userRepository.findByEmail(email).orElse(new User());
        user.setEmail(email);
        user.setFullName(name);
        if (user.getId() == null) {
            user.setId(googleId);
            user.setUsername(email);
            user.setPassword("");
            user.setProfileId(UUID.randomUUID().toString());
        }
        userRepository.save(user);

        AuthenticationService.TokenInfo tokenInfo = authenticationService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("access_token", tokenInfo.token());
        response.put("expiry_time", tokenInfo.expiryDate());
        response.put("email", email);
        response.put("name", name);
        return response;
    }

    // Phương thức hỗ trợ xác minh access_token (cho Implicit Flow)
    private Map<String, Object> verifyGoogleToken(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to verify Google token: " + response.getStatusCode());
        }
    }
}
