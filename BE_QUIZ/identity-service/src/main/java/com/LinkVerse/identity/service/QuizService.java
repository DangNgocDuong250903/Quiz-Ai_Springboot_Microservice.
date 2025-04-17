package com.LinkVerse.identity.service;

import com.LinkVerse.identity.dto.request.AnswerSubmissionRequest;
import com.LinkVerse.identity.dto.request.QuizSubmitRequest;
import com.LinkVerse.identity.dto.response.*;
import com.LinkVerse.identity.entity.*;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.exception.ErrorCode;
import com.LinkVerse.identity.repository.*;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Builder
public class QuizService {

    QuizRepository quizRepository;
    SubjectRepository subjectRepository;
    QuestionRepository questionRepository;
    AnswerRepository answerRepository;
    QuizSubmissionRepository submissionRepository;
    AnswerSubmissionRepository answerSubmissionRepository;
    UserRepository userRepository;

    public List<QuizResponse> getQuizzesBySubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        return quizRepository.findBySubject(subject).stream()
                .map(quiz -> QuizResponse.builder()
                        .id(quiz.getId())
                        .title(quiz.getTitle())
                        .subjectName(subject.getName())
                        .durationMinutes(quiz.getDurationMinutes())
                        .hasNoTimeLimit(Boolean.TRUE.equals(quiz.getHasNoTimeLimit()))
                        .build())
                .collect(Collectors.toList());
    }

    public QuizSubmissionResponse startQuiz(Long quizId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User user = userRepository.findUserById(userId);

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        QuizSubmission submission = QuizSubmission.builder()
                .user(user)
                .quiz(quiz)
                .startedAt(LocalDateTime.now())
                .isAutoSubmitted(false)
                .build();

        submission = submissionRepository.save(submission);

        List<Question> questions = questionRepository.findByQuiz(quiz);

        List<QuestionResponse> questionResponses = questions.stream().map(q ->
                QuestionResponse.builder()
                        .id(q.getId())
                        .content(q.getContent())
                        .answers(q.getAnswers().stream()
                                .map(a -> AnswerResponse.builder()
                                        .id(a.getId())
                                        .content(a.getContent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());

        return QuizSubmissionResponse.builder()
                .submissionId(submission.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .startedAt(submission.getStartedAt())
                .questions(questionResponses)
                .build();
    }

    public QuizResultResponse submitQuiz(QuizSubmitRequest request) {
    QuizSubmission submission = submissionRepository.findById(request.getSubmissionId())
            .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

    if (submission.getSubmittedAt() != null) {
        throw new AppException(ErrorCode.QUIZ_ALREADY_SUBMITTED);
    }

    List<QuizAnswerResult> answerResults = new ArrayList<>();
    int correctCount = 0;

    for (AnswerSubmissionRequest item : request.getAnswers()) {
        Question question = questionRepository.findById(item.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        Answer selected = answerRepository.findById(item.getAnswerId())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        boolean isCorrect = selected.getIsCorrect();
        if (isCorrect) correctCount++;
        String correctAnswerText = question.getAnswers().stream()
                .filter(Answer::getIsCorrect)
                .findFirst()
                .map(Answer::getContent)
                .orElse("Không rõ");
        answerResults.add(QuizAnswerResult.builder()
                .questionId(question.getId().toString())
                .questionContent(question.getContent())
                .selectedAnswer(selected.getContent())
                .isCorrect(isCorrect)
                .correctAnswer(correctAnswerText)
                .build());

        answerSubmissionRepository.save(
                AnswerSubmission.builder()
                        .submission(submission)
                        .question(question)
                        .selectedAnswer(selected)
                        .build()
        );
    }

    submission.setSubmittedAt(LocalDateTime.now());
    submission.setAnswers(null);
    submissionRepository.save(submission);

    return QuizResultResponse.builder()
            .submissionId(submission.getId().toString())
            .quizTitle(submission.getQuiz().getTitle())
            .totalQuestions(answerResults.size())
            .correctAnswers(correctCount)
            .scorePercent((double) correctCount * 100 / answerResults.size())
            .results(answerResults)
            .build();
}
public List<SubjectResponse> getAllSubjects() {
    return subjectRepository.findAll().stream()
            .map(subject -> SubjectResponse.builder()
                    .id(subject.getId())
                    .name(subject.getName())
                    .build())
            .collect(Collectors.toList());
}

//get all quizzes
    public List<QuizResponse> getAllQuizzes(){
        return quizRepository.findAll().stream()
                .map(quiz -> QuizResponse.builder()
                        .id(quiz.getId())
                        .title(quiz.getTitle())
                        .subjectName(quiz.getSubject().getName())
                        .durationMinutes(quiz.getDurationMinutes())
                        .hasNoTimeLimit(Boolean.TRUE.equals(quiz.getHasNoTimeLimit()))
                        .build())
                .collect(Collectors.toList());
    }
//getallAnswer
    public List<AnswerResponse> getAllAnswers(){
        return answerRepository.findAll().stream()
                .map(answer -> AnswerResponse.builder()
                        .id(answer.getId())
                        .content(answer.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(question -> QuestionResponse.builder()
                        .id(question.getId())
                        .content(question.getContent())
                        .answers(question.getAnswers().stream()
                                .map(answer -> AnswerResponse.builder()
                                        .id(answer.getId())
                                        .content(answer.getContent())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }
}
