package com.LinkVerse.identity.dto.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnswerRequest {
    Long questionId;
    String content;
    Boolean isCorrect;
}
