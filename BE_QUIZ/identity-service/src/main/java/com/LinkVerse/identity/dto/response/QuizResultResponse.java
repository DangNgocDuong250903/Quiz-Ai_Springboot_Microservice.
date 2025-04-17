package com.LinkVerse.identity.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResultResponse {
    String submissionId;
    String quizTitle;
    int totalQuestions;
    int correctAnswers;
    double scorePercent;
    List<QuizAnswerResult> results;
}
