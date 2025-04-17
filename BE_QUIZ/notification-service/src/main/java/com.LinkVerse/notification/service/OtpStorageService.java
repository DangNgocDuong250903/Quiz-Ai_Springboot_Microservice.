package com.LinkVerse.notification.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
public class OtpStorageService {
    private final Cache<String, String> otpStorage = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // Expire after 5 minutes
            .build();

    private final Cache<String, Integer> otpCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // Expire after 5 minutes
            .build();

    public void storeSecretKeyForUser(String userId, String secretKey) {
        otpStorage.put(userId, secretKey); // Store secretKey in cache
    }

    public String getSecretKeyForUser(String userId) {
        return otpStorage.getIfPresent(userId); // Retrieve secretKey from cache
    }

    public void storeOtpForUser(String userId, int otp) {
        otpCache.put(userId, otp); // Store OTP in cache
    }

    public Integer getOtpForUser(String userId) {
        return otpCache.getIfPresent(userId); // Retrieve OTP from cache
    }
}
