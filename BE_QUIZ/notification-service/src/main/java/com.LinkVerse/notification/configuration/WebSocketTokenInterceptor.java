package com.LinkVerse.notification.configuration;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTokenInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // Bạn có thể truy cập và kiểm tra thông tin từ message headers
        var token = message.getHeaders().get("simpSessionAttributes");
        return message;
    }
}
