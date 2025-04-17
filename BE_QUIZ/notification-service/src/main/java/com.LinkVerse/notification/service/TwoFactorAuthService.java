// package com.LinkVerse.notification.service;
//
// import com.LinkVerse.notification.entity.TwoFactorAuthData;
// import com.LinkVerse.notification.entity.User;
// import com.LinkVerse.notification.repository.TwoFactorAuthRepository;
// import com.LinkVerse.notification.repository.UserRepository;
// import org.springframework.stereotype.Service;
//
// import java.time.Duration;
// import java.time.LocalDateTime;
//
// @Service
// public class TwoFactorAuthService {
//    private static final int EXPIRATION_HOURS = 3;
//    private final TwoFactorAuthRepository repository;
//    private final UserRepository userRepository;
//
//    public TwoFactorAuthService(TwoFactorAuthRepository repository, UserRepository userRepository) {
//        this.repository = repository;
//        this.userRepository = userRepository;
//    }
//
//    public boolean validate2FACode(String userId, String code) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//        if (!user.isTwoFactorEnabled()) {
//            return true;
//        }
//
//        TwoFactorAuthData authData = repository.findByUserId(userId);
//        if (authData == null) {
//            return false;
//        }
//        boolean isCodeValid = authData.getCode().equals(code);
//        boolean isCodeExpired = Duration.between(authData.getTimestamp(), LocalDateTime.now()).toHours() >=
// EXPIRATION_HOURS;
//        return isCodeValid && !isCodeExpired;
//    }
// }
