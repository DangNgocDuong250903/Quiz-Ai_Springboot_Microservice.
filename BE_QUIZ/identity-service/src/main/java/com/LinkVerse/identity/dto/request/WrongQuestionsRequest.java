package com.LinkVerse.identity.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class WrongQuestionsRequest {
    private List<String> wrongQuestions;
}
