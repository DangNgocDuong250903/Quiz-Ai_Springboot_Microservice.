package com.LinkVerse.identity.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.LinkVerse.identity.entity.DeviceInfo;
import com.LinkVerse.identity.entity.LoginHistory;
import com.LinkVerse.identity.entity.User;
import com.LinkVerse.identity.repository.LoginHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public void recordLogin(User user, DeviceInfo deviceInfo) {
        LoginHistory loginHistory =
                LoginHistory.builder().user(user).deviceInfo(deviceInfo).build();

        loginHistoryRepository.save(loginHistory);
        log.info("Recorded login for user: {}, device: {}", user.getId(), deviceInfo.getDeviceId());
    }

    public Page<LoginHistory> getUserLoginHistory(String userId, Pageable pageable) {
        return loginHistoryRepository.findByUserId(userId, pageable);
    }

    public Page<LoginHistory> getCurrentUserLoginHistory(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = authentication.getName();

        return getUserLoginHistory(currentUserId, pageable);
    }
}
