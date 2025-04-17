package com.LinkVerse.identity.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuestionRequest {
    Long quizId;
    String content;
}
