package com.LinkVerse.identity.dto.request;

import lombok.Data;

@Data
public class UpdateQuestionRequest {
    private Long questionId;
    private String newContent;
}
