package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResponse {
    Long id;
    String title;
    String subjectName;
    Integer durationMinutes;
    boolean hasNoTimeLimit;
}
