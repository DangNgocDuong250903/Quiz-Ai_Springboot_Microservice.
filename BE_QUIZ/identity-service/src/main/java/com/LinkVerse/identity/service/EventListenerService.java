package com.LinkVerse.identity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.LinkVerse.event.dto.NotificationEvent;
import com.LinkVerse.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventListenerService {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "user-creation-event", groupId = "notification-group")
    public void handleUserCreationEvent(NotificationEvent event) {
        List<String> userTokens = getAllUserTokens();
        notificationService.sendNotificationToUsers(userTokens, event.getSubject(), event.getBody());
    }

    private List<String> getAllUserTokens() {
        return userRepository.findAll().stream()
                .map(user -> user.getFcmToken())
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());
    }
}
