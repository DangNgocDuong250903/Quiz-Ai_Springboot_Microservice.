package com.LinkVerse.identity.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateQuizRequest {
    String title;
    Long subjectId;
    Integer durationMinutes;
    Boolean hasNoTimeLimit;
    LocalDateTime expirationTime;
}
