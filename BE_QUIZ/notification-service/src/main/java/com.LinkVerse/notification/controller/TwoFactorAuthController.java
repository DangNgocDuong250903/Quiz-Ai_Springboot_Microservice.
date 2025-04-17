package com.LinkVerse.notification.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.LinkVerse.notification.entity.User;
import com.LinkVerse.notification.exception.AppException;
import com.LinkVerse.notification.exception.ErrorCode;
import com.LinkVerse.notification.service.EmailService;
import com.LinkVerse.notification.service.OtpService;

import feign.FeignException;

@RestController
@RequestMapping("/2fa")
public class TwoFactorAuthController {
    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateOtp(@RequestParam String email) {
        try {
            int otp = otpService.generateOtp(email);
            if (otp == 0) {
                return ResponseEntity.status(500).body("Error generating OTP");
            }

            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("OTP sent to user's email");
        } catch (FeignException.TooManyRequests e) {
            return ResponseEntity.status(429)
                    .body("{\"code\": " + ErrorCode.TOO_MANY_REQUESTS.getCode() + ", \"message\": \""
                            + ErrorCode.TOO_MANY_REQUESTS.getMessage() + "\"}");
        } catch (AppException e) {
            return ResponseEntity.status(
                            ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode().value())
                    .body("{\"code\": " + ErrorCode.UNCATEGORIZED_EXCEPTION.getCode() + ", \"message\": \""
                            + ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage() + "\"}");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateOtp(@RequestParam String identifier, @RequestParam int otp) {
        User user;
        if (identifier.contains("@")) {
            user = emailService.getUserByEmail(identifier);
        } else {
            user = emailService.getUserByUsername(identifier);
        }

        if (user == null) {
            return ResponseEntity.status(400).body("User not found for the given identifier");
        }

        return otpService.isOtpValid(user, otp)
                ? ResponseEntity.ok("OTP is valid")
                : ResponseEntity.status(400).body("Invalid OTP");
    }
}
