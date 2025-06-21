package com.LinkVerse.identity.dto.request;

import lombok.Data;

@Data
public class UpdateAnswerRequest {
    private Long answerId;
    private String newContent;
    private Boolean isCorrect;
}
