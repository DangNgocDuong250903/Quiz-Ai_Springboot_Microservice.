package com.LinkVerse.notification.controller;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.LinkVerse.event.dto.NotificationEvent;
import com.LinkVerse.notification.dto.request.Recipient;
import com.LinkVerse.notification.dto.request.SendEmailRequest;
import com.LinkVerse.notification.repository.UserRepository;
import com.LinkVerse.notification.service.EmailService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {

    EmailService emailService;
    private final UserRepository userRepository;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message) {
        log.info("Message received: {}", message);
        emailService.sendEmail(SendEmailRequest.builder()
                .to(Recipient.builder().email(message.getRecipient()).build())
                .subject(message.getSubject())
                .htmlContent(message.getBody())
                .build());
    }
}
