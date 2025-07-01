package com.LinkVerse.identity.service.report;

import com.LinkVerse.identity.dto.response.QuizReportResponse;
import com.LinkVerse.identity.entity.AnswerSubmission;
import com.LinkVerse.identity.entity.Question;
import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.entity.QuizSubmission;
import com.LinkVerse.identity.exception.AppException;
import com.LinkVerse.identity.exception.ErrorCode;
import com.LinkVerse.identity.repository.QuestionRepository;
import com.LinkVerse.identity.repository.QuizRepository;
import com.LinkVerse.identity.repository.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizReportService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    public QuizReportResponse generateReport(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuiz(quiz);

        // Tính điểm trung bình
        double avgScore = submissions.stream()
                .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0.0)
                .average()
                .orElse(0.0);

        // Tổng lượt nộp bài
        int totalSubmitted = submissions.size();

        // Số người đã bắt đầu làm bài (startedAt != null)
        int totalStarted = quizSubmissionRepository.countByQuizIdAndStartedAtIsNotNull(quizId);

        // Tỉ lệ hoàn thành
        double completionRate = (totalStarted == 0)
                ? 100.0
                : ((double) totalSubmitted / totalStarted) * 100;

        // Tìm câu bị sai nhiều nhất
        Map<Long, Integer> wrongMap = new HashMap<>();
        for (QuizSubmission submission : submissions) {
            if (submission.getAnswers() == null) continue;
            for (AnswerSubmission answer : submission.getAnswers()) {
                if (answer.getSelectedAnswer() != null &&
                    Boolean.FALSE.equals(answer.getSelectedAnswer().getIsCorrect())) {
                    Long qId = answer.getSelectedAnswer().getQuestion().getId();
                    wrongMap.put(qId, wrongMap.getOrDefault(qId, 0) + 1);
                }
            }
        }

        Long mostWrongQid = wrongMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        String mostWrongContent = (mostWrongQid != null)
                ? questionRepository.findById(mostWrongQid)
                    .map(Question::getContent)
                    .orElse("Không rõ")
                : "Không có dữ liệu";

        return QuizReportResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalSubmission(totalSubmitted)
                .averageScore(avgScore)
                .completionRate(completionRate)
                .mostWrongQuestion(mostWrongContent)
                .build();
    }
}
