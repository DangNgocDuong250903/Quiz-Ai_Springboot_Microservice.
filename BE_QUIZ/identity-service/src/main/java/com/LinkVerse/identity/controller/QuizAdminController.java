package com.LinkVerse.identity.controller;

import com.LinkVerse.identity.dto.request.ApiResponse;
import com.LinkVerse.identity.dto.request.*;
import com.LinkVerse.identity.service.QuizAdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizAdminController {

    QuizAdminService quizAdminService;

    @PostMapping("/subjects")
    public ApiResponse<String> createSubject(@RequestBody @Valid CreateSubjectRequest request) {
        quizAdminService.createSubject(request);
        return ApiResponse.<String>builder().result("Subject created").build();
    }

    @PostMapping("/quizzes")
    public ApiResponse<String> createQuiz(@RequestBody @Valid CreateQuizRequest request) {
        quizAdminService.createQuiz(request);
        return ApiResponse.<String>builder().result("Quiz created").build();
    }

    @PostMapping("/questions")
    public ApiResponse<String> createQuestion(@RequestBody @Valid CreateQuestionRequest request) {
        quizAdminService.createQuestion(request);
        return ApiResponse.<String>builder().result("Question added").build();
    }

    @PostMapping("/answers")
    public ApiResponse<String> createAnswer(@RequestBody @Valid CreateAnswerRequest request) {
        quizAdminService.createAnswer(request);
        return ApiResponse.<String>builder().result("Answer added").build();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/subjects/{subjectId}")
public ResponseEntity<String> deleteSubject(@PathVariable Long subjectId) {
    quizAdminService.deleteSubject(subjectId);
    return ResponseEntity.ok("Subject deleted successfully");
}

}
