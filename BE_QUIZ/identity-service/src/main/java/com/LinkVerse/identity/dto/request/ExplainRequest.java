package com.LinkVerse.identity.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ExplainRequest {
    private String questionContent;
    private List<String> options;
    private String correctAnswerLabel;
}
