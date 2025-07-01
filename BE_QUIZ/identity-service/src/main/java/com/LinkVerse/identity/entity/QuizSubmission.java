package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    User user;

    @ManyToOne
    Quiz quiz;

    LocalDateTime startedAt;
    LocalDateTime submittedAt;
    Boolean isAutoSubmitted = false; // Hệ thống tự nộp khi hết giờ

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL)
    List<AnswerSubmission> answers;
    Double score;
}
