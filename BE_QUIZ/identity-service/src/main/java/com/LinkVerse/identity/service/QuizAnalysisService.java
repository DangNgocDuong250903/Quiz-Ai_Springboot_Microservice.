package com.LinkVerse.identity.service;

import com.LinkVerse.identity.repository.AnswerSubmissionRepository;
import com.LinkVerse.identity.repository.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizAnalysisService {

    final AnswerSubmissionRepository answerSubmissionRepository;
    final QuizSubmissionRepository quizSubmissionRepository;

    public Map<String, Object> getStatistics() {
        Map<Long, Long> countCorrect = new HashMap<>();
        Map<Long, Long> countTotal = new HashMap<>();

        for (var sub : answerSubmissionRepository.findAll()) {
            Long qId = sub.getQuestion().getId();
            countTotal.put(qId, countTotal.getOrDefault(qId, 0L) + 1);
            if (sub.getSelectedAnswer().getIsCorrect()) {
                countCorrect.put(qId, countCorrect.getOrDefault(qId, 0L) + 1);
            }
        }

        Map<Long, Double> accuracy = new HashMap<>();
        for (Long qId : countTotal.keySet()) {
            accuracy.put(qId, (double) countCorrect.getOrDefault(qId, 0L) / countTotal.get(qId));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("questionAccuracy", accuracy);

        // Phân loại học sinh theo điểm
        Map<String, String> studentGroup = new HashMap<>();
        for (var quizSub : quizSubmissionRepository.findAll()) {
            int total = quizSub.getAnswers().size();
            int correct = (int) quizSub.getAnswers().stream().filter(a -> a.getSelectedAnswer().getIsCorrect()).count();
            double score = (double) correct / total;

            String userId = quizSub.getUser().getId();
            String level = score >= 0.8 ? "Giỏi" : score >= 0.5 ? "Trung bình" : "Yếu";
            studentGroup.put(userId, level);
        }

        result.put("studentGroups", studentGroup);
        return result;
    }
}
