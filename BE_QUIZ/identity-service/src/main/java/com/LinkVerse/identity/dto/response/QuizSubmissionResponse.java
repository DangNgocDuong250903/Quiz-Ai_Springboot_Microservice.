package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizSubmissionResponse {
    Long submissionId;
    Long quizId;
    String quizTitle;
    LocalDateTime startedAt;
    List<QuestionResponse> questions;
}
