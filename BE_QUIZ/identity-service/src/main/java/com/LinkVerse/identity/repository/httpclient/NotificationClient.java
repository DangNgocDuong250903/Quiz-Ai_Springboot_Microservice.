package com.LinkVerse.identity.repository.httpclient;

import com.LinkVerse.identity.configuration.AuthenticationRequestInterceptor;
import com.LinkVerse.identity.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "notification-service",
        url = "${app.services.notification}",
        configuration = {AuthenticationRequestInterceptor.class})
public interface NotificationClient {
    @PostMapping("/2fa/generate")
    ResponseEntity<String> generateOtp(@RequestParam("email") String email);

    @PostMapping("/2fa/validate")
    ResponseEntity<String> validateOtp(@RequestParam("identifier") String identifier, @RequestParam("otp") int otp);

    @GetMapping("/email/user/email")
    ResponseEntity<String> getUserEmail(@RequestParam("username") String username);

    @GetMapping("/email/user")
    ResponseEntity<User> getUserByEmail(@RequestParam("email") String email);

    @PostMapping("/email/send-login-notification")
    ResponseEntity<Void> sendLoginNotification(@RequestBody User user);
}
