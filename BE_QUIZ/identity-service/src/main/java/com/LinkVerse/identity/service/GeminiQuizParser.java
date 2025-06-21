package com.LinkVerse.identity.service;

import com.LinkVerse.identity.entity.Answer;
import com.LinkVerse.identity.entity.Question;
import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.entity.Subject;
import com.LinkVerse.identity.repository.AnswerRepository;
import com.LinkVerse.identity.repository.QuestionRepository;
import com.LinkVerse.identity.repository.QuizRepository;
import com.LinkVerse.identity.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiQuizParser {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SubjectRepository subjectRepository;

    public void saveQuizFromAIText(Long subjectId, String quizTitle, String aiText) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy môn học"));

        Quiz quiz = Quiz.builder()
                .title(quizTitle)
                .subject(subject)
                .durationMinutes(20)
                .hasNoTimeLimit(false)
                .expirationTime(null)
                .build();

        quiz = quizRepository.save(quiz);

        String[] blocks = aiText.split("\\n\\n");

        for (String block : blocks) {
            String[] lines = block.split("\\n");
            if (lines.length < 5) continue;

            String questionText = lines[0].replaceFirst("^\\d+\\.\\s*", "").trim();

            Question question = Question.builder()
                    .content(questionText)
                    .quiz(quiz)
                    .build();

            question = questionRepository.save(question);

            for (int i = 1; i <= 4; i++) {
                String line = lines[i].trim();
                boolean isCorrect = line.contains("✅");
                String answerText = line.replace("✅", "").trim();

                Answer answer = Answer.builder()
                        .content(answerText.substring(2).trim())
                        .isCorrect(isCorrect)
                        .question(question)
                        .build();

                answerRepository.save(answer);
            }
        }
    }
}
