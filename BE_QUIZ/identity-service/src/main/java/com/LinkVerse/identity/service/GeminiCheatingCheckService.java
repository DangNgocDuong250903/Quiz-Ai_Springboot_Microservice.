package com.LinkVerse.identity.service;

import com.LinkVerse.identity.entity.QuizSubmission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiCheatingCheckService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public String analyzeCheating(double timeInMinutes, int tabSwitchCount, double scorePercent, boolean isNewUser) {
        String prompt = buildCheatingPrompt(timeInMinutes, tabSwitchCount, scorePercent, isNewUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(geminiApiUrl, entity, Map.class);
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return parts.get(0).get("text").toString();
        } catch (Exception e) {
            log.error("Lỗi gọi Gemini để phân tích gian lận", e);
            return "Không thể đánh giá gian lận từ AI.";
        }
    }

    private String buildCheatingPrompt(double timeInMinutes, int tabSwitchCount, double scorePercent, boolean isNewUser) {
        return """
Dưới đây là hành vi học sinh khi làm bài kiểm tra:

- Thời gian làm bài: %.1f phút
- Số lần chuyển tab: %d
- Tỉ lệ đúng: %.2f%%
- Tài khoản mới tạo: %s

Hãy đánh giá xem học sinh có đang gian lận hay không. Trả lời ngắn gọn, có lý do.
""".formatted(timeInMinutes, tabSwitchCount, scorePercent, isNewUser ? "Có" : "Không");
    }
        public String evaluateCheatingFromSubmission(QuizSubmission submission, int tabSwitchCount, boolean isNewUser) {
        int total = submission.getAnswers().size();
        int correct = (int) submission.getAnswers().stream()
                .filter(a -> a.getSelectedAnswer().getIsCorrect()).count();

        double scorePercent = (double) correct * 100 / total;
        double timeInMinutes = java.time.Duration.between(submission.getStartedAt(), submission.getSubmittedAt()).toMinutes();

        return analyzeCheating(timeInMinutes, tabSwitchCount, scorePercent, isNewUser);
    }
}
