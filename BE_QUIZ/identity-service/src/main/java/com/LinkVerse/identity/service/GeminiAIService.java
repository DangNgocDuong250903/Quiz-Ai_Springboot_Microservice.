package com.LinkVerse.identity.service;

import com.LinkVerse.identity.dto.response.QuizAnswerResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAIService {

    private final RestTemplate restTemplate = new RestTemplate();

//    @Value("${gemini.api.key}")
//    private String geminiApiKey;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public String getQuizFeedbackFromAI(List<QuizAnswerResult> answers) {
        String prompt = buildPromptFromAnswers(answers);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(geminiApiUrl, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("candidates")) {
                return "Không có phản hồi từ AI.";
            }

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseBody.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Không có ứng viên phản hồi từ AI.";
            }

            Map<String, Object> firstCandidate = candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");

            if (parts != null && !parts.isEmpty() && parts.get(0).containsKey("text")) {
                return parts.get(0).get("text").toString();
            }

            return "Không có nội dung từ AI.";
        } catch (Exception e) {
            log.error("Lỗi khi gọi Gemini API", e);
            return "Không thể lấy phản hồi từ AI.";
        }
    }

    private String buildPromptFromAnswers(List<QuizAnswerResult> answers) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Dưới đây là kết quả làm bài trắc nghiệm của một học sinh. Hãy đưa ra nhận xét tổng quát về điểm mạnh, điểm yếu và gợi ý cải thiện:\n\n");

        for (QuizAnswerResult result : answers) {
            prompt.append("- Câu hỏi: ").append(result.getQuestionContent()).append("\n");
            prompt.append("  - Trả lời: ").append(result.getSelectedAnswer()).append(" (")
                    .append(result.getIsCorrect() ? "Đúng" : "Sai").append(")\n");  // ✅ isCorrect() thay vì getIsCorrect()
            prompt.append("  - Đáp án đúng: ").append(result.getCorrectAnswer()).append("\n\n");
        }

        return prompt.toString();
    }
}
