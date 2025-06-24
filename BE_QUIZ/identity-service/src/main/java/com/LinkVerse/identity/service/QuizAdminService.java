package com.LinkVerse.identity.service;

import com.LinkVerse.identity.dto.request.*;
import com.LinkVerse.identity.entity.*;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.exception.ErrorCode;
import com.LinkVerse.identity.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizAdminService {

    SubjectRepository subjectRepository;
    QuizRepository quizRepository;
    QuestionRepository questionRepository;
    AnswerRepository answerRepository;
    AnswerSubmissionRepository answerSubmissionRepository;
    QuizSubmissionRepository quizSubmissionRepository;
    GeminiQuestionGeneratorService geminiQuestionGeneratorService;
    GeminiQuizParser geminiQuizParser;

    public ApiResponse<String> generateAutoQuiz(AutoQuizRequest request) {
        String resultText = geminiQuestionGeneratorService.generateQuestions(
                request.getTopic(), request.getNumQuestions());

        if (resultText == null || resultText.contains("Không thể")) {
            throw new RuntimeException("Không nhận được dữ liệu hợp lệ từ Gemini.");
        }

        geminiQuizParser.saveQuizFromAIText(request.getSubjectId(), request.getQuizTitle(), resultText);

        return ApiResponse.<String>builder()
                .message("Tạo và lưu đề thi tự động thành công.")
                .result(resultText)
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> createSubject(CreateSubjectRequest request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ALREADY_EXISTED);
        }

        Subject subject = new Subject();
        subject.setName(request.getName());
        subjectRepository.save(subject);

        return ApiResponse.<String>builder()
                .message("Subject created successfully")
                .result(subject.getName())
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> createQuiz(CreateQuizRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .subject(subject)
                .durationMinutes(request.getDurationMinutes())
                .hasNoTimeLimit(request.getHasNoTimeLimit())
                .expirationTime(request.getExpirationTime())
                .build();

        quizRepository.save(quiz);

        return ApiResponse.<String>builder()
                .message("Quiz created successfully")
                .result(quiz.getTitle())
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> createQuestion(CreateQuestionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Question question = new Question();
        question.setContent(request.getContent());
        question.setQuiz(quiz);
        questionRepository.save(question);

        return ApiResponse.<String>builder()
                .message("Question created successfully")
                .result(question.getContent())
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> createAnswer(CreateAnswerRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Answer answer = new Answer();
        answer.setContent(request.getContent());
        answer.setIsCorrect(Boolean.TRUE.equals(request.getIsCorrect()));
        answer.setQuestion(question);
        answerRepository.save(answer);

        return ApiResponse.<String>builder()
                .message("Answer created successfully")
                .result(answer.getContent())
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> updateQuestion(UpdateQuestionRequest request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        question.setContent(request.getNewContent());
        questionRepository.save(question);

        return ApiResponse.<String>builder()
                .message("Question updated successfully")
                .result(question.getContent())
                .build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> updateAnswer(UpdateAnswerRequest request) {
        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        answer.setContent(request.getNewContent());
        answer.setIsCorrect(Boolean.TRUE.equals(request.getIsCorrect()));
        answerRepository.save(answer);

        return ApiResponse.<String>builder()
                .message("Answer updated successfully")
                .result(answer.getContent())
                .build();
    }
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public ApiResponse<List<Subject>> getAllSubject() {
    List<Subject> subjects = subjectRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    return ApiResponse.<List<Subject>>builder()
            .message("Danh sách môn học được truy xuất thành công")
            .result(subjects)
            .build();
}


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<String> deleteSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        List<Quiz> quizzes = quizRepository.findBySubject(subject);

        for (Quiz quiz : quizzes) {
            List<Question> questions = questionRepository.findByQuiz(quiz);

            for (Question question : questions) {
                List<Answer> answers = answerRepository.findByQuestion(question);

                for (Answer answer : answers) {
                    List<AnswerSubmission> submissions = answerSubmissionRepository.findBySelectedAnswer(answer);
                    answerSubmissionRepository.deleteAll(submissions);
                }

                answerRepository.deleteAll(answers);
            }

            questionRepository.deleteAll(questions);

            List<QuizSubmission> submissions = quizSubmissionRepository.findByQuiz(quiz);
            quizSubmissionRepository.deleteAll(submissions);
        }

        quizRepository.deleteAll(quizzes);
        subjectRepository.delete(subject);

        return ApiResponse.<String>builder()
                .message("Subject deleted successfully")
                .result("Deleted subject ID: " + subjectId)
                .build();
    }
}
