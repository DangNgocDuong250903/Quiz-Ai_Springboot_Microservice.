package com.LinkVerse.identity.controller;

import com.LinkVerse.identity.dto.response.QuizReportResponse;

import com.LinkVerse.identity.service.report.QuizReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/quiz-report")
@RequiredArgsConstructor
public class QuizReportController {

    private final QuizReportService quizReportService;

    // API: GET /api/quiz-report/{quizId}
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizReportResponse> getReport(@PathVariable Long quizId) {
        QuizReportResponse report = quizReportService.generateReport(quizId);
        return ResponseEntity.ok(report);
    }
}
