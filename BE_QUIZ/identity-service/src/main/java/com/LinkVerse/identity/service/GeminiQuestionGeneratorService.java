package com.LinkVerse.identity.service;

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
public class GeminiQuestionGeneratorService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    public String generateQuestions(String topic, int numQuestions) {
        String prompt = buildPrompt(topic, numQuestions);

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
            log.error("Lỗi gọi Gemini", e);
            return null;
        }
    }

    private String buildPrompt(String topic, int numQuestions) {
        return "Hãy tạo " + numQuestions + " câu hỏi trắc nghiệm về chủ đề \"" + topic + "\".\n"
                + "- Mỗi câu có 4 phương án A, B, C, D.\n"
                + "- Đánh dấu phương án đúng bằng ✅ hoặc ghi rõ 'Đáp án đúng: ...'";
    }

    public String explainQuestion(String question, List<String> options, String correctAnswerLabel) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Giải thích vì sao câu hỏi: '").append(question).append("' có đáp án đúng là ").append(correctAnswerLabel).append(".\n");

        char label = 'A';
        for (String opt : options) {
            prompt.append(label).append(". ").append(opt).append("\n");
            label++;
        }

        prompt.append("Giải thích chi tiết vì sao ").append(correctAnswerLabel).append(" đúng, các đáp án còn lại sai.");
        return generateExplanation(prompt.toString());
    }

    public String recommendLearningMaterials(List<String> wrongQuestions) {
        StringBuilder prompt = new StringBuilder("Tôi đã làm sai các câu sau:\n");
        for (String q : wrongQuestions) {
            prompt.append("- ").append(q).append("\n");
        }
        prompt.append("Hãy gợi ý tài liệu học phù hợp (video, bài đọc, sách).");
        return generateExplanation(prompt.toString());
    }

    public String generateExplanation(String prompt) {
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
            log.error("Lỗi gọi Gemini khi giải thích/gợi ý", e);
            return "Không thể tạo phản hồi từ AI.";
        }
    }

}

