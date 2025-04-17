package com.LinkVerse.identity.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {
    private Integer id;
    private String chatName;
    private String chatImage;
    private boolean isGroup;
    private Set<UserResponse> users;
}
