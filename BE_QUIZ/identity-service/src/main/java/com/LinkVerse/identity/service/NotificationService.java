package com.LinkVerse.identity.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendNotificationToUsers(List<String> tokens, String title, String body) {
        for (String token : tokens) {
            if (token == null || token.isEmpty()) {
                logger.warn("Skipping empty or null token");
                continue;
            }

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                logger.info("Successfully sent message to token {}: {}", token, response);
            } catch (Exception e) {
                logger.error("Error sending message to token {}", token, e);
            }
        }
    }
}
