package com.LinkVerse.identity.dto.request;

import lombok.Data;

@Data
public class AutoQuizRequest {
    private Long subjectId;
    private String quizTitle;
    private String topic;
    private int numQuestions;
}

