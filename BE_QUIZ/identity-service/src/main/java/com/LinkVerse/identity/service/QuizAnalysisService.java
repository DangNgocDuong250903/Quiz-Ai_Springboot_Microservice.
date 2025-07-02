package com.LinkVerse.identity.service;

import com.LinkVerse.identity.entity.Answer;
import com.LinkVerse.identity.entity.Question;
import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.repository.AnswerSubmissionRepository;
import com.LinkVerse.identity.repository.QuizRepository;
import com.LinkVerse.identity.repository.QuizSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizAnalysisService {
    final QuizRepository quizRepository;
    final AnswerSubmissionRepository answerSubmissionRepository;
    final QuizSubmissionRepository quizSubmissionRepository;
    final GeminiAIService geminiAIService;
    final QuizStatsRepository quizStatsRepository;
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
private String generatePromptForQuizAnalysis(Long quizId) {
        Quiz quiz = quizRepository.findQuizWithQuestionsAndAnswers(quizId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đề thi"));

        StringBuilder prompt = new StringBuilder();

        prompt.append("Phân tích đề thi: ").append(quiz.getTitle()).append("\n");
        prompt.append("Môn học: ").append(quiz.getSubject().getName()).append("\n");
        prompt.append("Thời lượng: ").append(quiz.getDurationMinutes()).append(" phút\n");
        prompt.append("Tổng số câu hỏi: ").append(quiz.getQuestions().size()).append("\n\n");

        int index = 1;
        for (Question question : quiz.getQuestions()) {
            prompt.append(index++).append(". ").append(question.getContent()).append("\n");

            char opt = 'A';
            for (Answer answer : question.getAnswers()) {
                prompt.append("   ").append(opt++).append(". ").append(answer.getContent());
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    prompt.append(" ✅");
                }
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("Hãy đánh giá độ khó, độ hợp lý, tính bao phủ kiến thức của đề thi và đề xuất cải tiến.");
        return prompt.toString();
    }




}
