package com.LinkVerse.identity.controller;

import com.LinkVerse.identity.dto.request.*;
import com.LinkVerse.identity.service.GeminiCheatingCheckService;
import com.LinkVerse.identity.service.GeminiQuestionGeneratorService;
import com.LinkVerse.identity.service.GeminiQuizParser;
import com.LinkVerse.identity.service.QuizAnalysisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Ai/quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizAIController {
      GeminiQuestionGeneratorService geminiQuestionGeneratorService;
      GeminiQuizParser geminiQuizParser;
    final GeminiQuestionGeneratorService geminiService;
    final QuizAnalysisService quizAnalysisService;
    final GeminiCheatingCheckService geminiCheatingCheckService;
    @PostMapping("/analyze-cheating")
public ApiResponse<String> checkCheating(@RequestBody CheatingAnalysisRequest request) {
    String result = geminiCheatingCheckService.analyzeCheating(
        request.getTimeInMinutes(),
        request.getTabSwitchCount(),
        request.getScorePercent(),
        request.isNewUser()
    );

    return ApiResponse.<String>builder()
        .message("Phân tích AI gian lận")
        .result(result)
        .build();
}

    @PostMapping("/auto-generate")
    public ApiResponse<String> generateAndSaveQuiz(@RequestBody AutoQuizRequest request) {
        String result = geminiQuestionGeneratorService.generateQuestions(
                request.getTopic(),
                request.getNumQuestions()
        );

        if (result == null || result.contains("Không thể")) {
            throw new RuntimeException("Gemini không trả về dữ liệu hợp lệ.");
        }

        geminiQuizParser.saveQuizFromAIText(request.getSubjectId(), request.getQuizTitle(), result);

        return ApiResponse.<String>builder()
                .message("Đã tạo và lưu đề thi tự động.")
                .result(result)
                .build();
    }
      // Trợ lý học tập: Giải thích câu hỏi
    @PostMapping("/explain")
    public ApiResponse<String> explain(@RequestBody ExplainRequest request) {
        String result = geminiService.explainQuestion(request.getQuestionContent(), request.getOptions(), request.getCorrectAnswerLabel());
        return ApiResponse.<String>builder().message("Giải thích câu hỏi").result(result).build();
    }

    // Thống kê độ khó câu hỏi & phân loại học sinh
    @GetMapping("/statistics")
    public ApiResponse<Object> analyzeStatistics() {
        return ApiResponse.builder()
                .message("Thống kê AI")
                .result(quizAnalysisService.getStatistics())
                .build();
    }

    // Gợi ý học liệu dựa trên câu sai
    @PostMapping("/recommend")
    public ApiResponse<String> recommend(@RequestBody WrongQuestionsRequest request) {
        String result = geminiService.recommendLearningMaterials(request.getWrongQuestions());
        return ApiResponse.<String>builder().message("Tài liệu gợi ý").result(result).build();
    }
}
