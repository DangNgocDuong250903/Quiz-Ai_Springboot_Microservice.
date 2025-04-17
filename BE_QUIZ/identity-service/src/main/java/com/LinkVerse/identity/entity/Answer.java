package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;

     @Column(name = "is_correct", nullable = false)
    Boolean isCorrect;

    @ManyToOne
    Question question;
}
