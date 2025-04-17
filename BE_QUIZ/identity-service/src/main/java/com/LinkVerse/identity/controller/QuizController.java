package com.LinkVerse.identity.controller;

import com.LinkVerse.identity.dto.request.ApiResponse;
import com.LinkVerse.identity.dto.request.QuizSubmissionRequest;
import com.LinkVerse.identity.dto.request.QuizSubmitRequest;
import com.LinkVerse.identity.dto.response.*;
import com.LinkVerse.identity.service.QuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuizController {

    QuizService quizService;

//     * Lấy danh sách đề thi theo môn học
    @GetMapping("/by-subject/{subjectId}")
    public ApiResponse<List<QuizResponse>> getQuizzesBySubject(@PathVariable Long subjectId) {
        return ApiResponse.<List<QuizResponse>>builder()
                .result(quizService.getQuizzesBySubject(subjectId))
                .build();
    }


//     * Người dùng bắt đầu làm bài

    @PostMapping("/start")
    public ApiResponse<QuizSubmissionResponse> startQuiz(@RequestBody @Valid QuizSubmissionRequest request) {
        return ApiResponse.<QuizSubmissionResponse>builder()
                .result(quizService.startQuiz(request.getQuizId()))
                .build();
    }

//     * Người dùng nộp bài thi
    @PostMapping("/submit")
public ApiResponse<QuizResultResponse> submitQuiz(@RequestBody @Valid QuizSubmitRequest request) {
    return ApiResponse.<QuizResultResponse>builder()
            .result(quizService.submitQuiz(request))
            .build();
}
// *Getall môn học
    @GetMapping("/subjects")
public ApiResponse<List<SubjectResponse>> getAllSubjects() {
    return ApiResponse.<List<SubjectResponse>>builder()
            .result(quizService.getAllSubjects())
            .build();
}
    @GetMapping("/quizzes")
    public ApiResponse<List<QuizResponse>> getAllQuizzes() {
        return ApiResponse.<List<QuizResponse>>builder()
                .result(quizService.getAllQuizzes())
                .build();
    }
     @GetMapping("/answers")
    public ApiResponse<List<AnswerResponse>> getAllAnswers() {
        return ApiResponse.<List<AnswerResponse>>builder()
                .result(quizService.getAllAnswers())
                .build();
    }

    @GetMapping("/questions")
    public ApiResponse<List<QuestionResponse>> getAllQuestions() {
        return ApiResponse.<List<QuestionResponse>>builder()
                .result(quizService.getAllQuestions())
                .build();
    }

}
