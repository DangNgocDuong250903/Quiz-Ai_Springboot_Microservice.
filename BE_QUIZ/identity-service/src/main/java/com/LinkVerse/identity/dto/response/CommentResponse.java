package com.LinkVerse.identity.dto.response;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponse {
    String id;
    String userId;
    String CommentId;
    String content;
    Instant createdDate;
    int like;
    int unlike;
}
