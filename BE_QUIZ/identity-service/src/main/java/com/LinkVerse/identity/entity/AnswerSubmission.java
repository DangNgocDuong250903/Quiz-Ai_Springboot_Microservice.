package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    QuizSubmission submission;

    @ManyToOne
    Question question;

    @ManyToOne
    Answer selectedAnswer;
}
