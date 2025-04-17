package com.LinkVerse.notification.service;

import org.springframework.stereotype.Service;

import com.LinkVerse.notification.entity.User;
import com.LinkVerse.notification.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
public class OtpService {
    private final GoogleAuthenticator gAuth;
    private final UserRepository userRepository;
    private final OtpStorageService otpStorageService;

    public OtpService(UserRepository userRepository, OtpStorageService otpStorageService) {
        this.userRepository = userRepository;
        this.otpStorageService = otpStorageService;
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setTimeStepSizeInMillis(30000)
                .setWindowSize(3)
                .build();
        this.gAuth = new GoogleAuthenticator(config);
    }

    public int generateOtp(String email) {
        String secretKey = generateSecretKey();
        int otp = getTotpPassword(secretKey);
        saveOtpSecretKey(email, secretKey);
        otpStorageService.storeOtpForUser(email, otp); // Luu otp vao cache de xu ly
        return otp;
    }

    public String getFormattedOtp(int otp) {
        return String.format("%06d", otp); // Cho ra ma otp co 6 chu so
    }

    public String generateAndFormatOtp(String email) {
        int otp = generateOtp(email);
        return getFormattedOtp(otp);
    }

    public void saveOtpSecretKey(String email, String secretKey) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setOtpSecretKey(secretKey);
        userRepository.save(user);
        otpStorageService.storeSecretKeyForUser(email, secretKey); // cache
    }

    public String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public int getTotpPassword(String secretKey) {
        return gAuth.getTotpPassword(secretKey);
    }

    public boolean isOtpValid(User user, int otp) {
        String secretKey = otpStorageService.getSecretKeyForUser(user.getEmail());
        if (secretKey == null) {
            throw new RuntimeException("No OTP secret key for this user");
        }
        return gAuth.authorize(secretKey, otp);
    }
}
