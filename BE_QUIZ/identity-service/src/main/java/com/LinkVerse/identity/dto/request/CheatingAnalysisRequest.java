package com.LinkVerse.identity.dto.request;

import lombok.Data;

@Data
public class CheatingAnalysisRequest {
    private double timeInMinutes;
    private int tabSwitchCount;
    private double scorePercent;
    private boolean newUser;
}

