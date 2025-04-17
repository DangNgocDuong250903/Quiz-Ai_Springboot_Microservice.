package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String name; // Lịch sử, Toán, Lý

    @OneToMany(mappedBy = "subject")
    List<Quiz> quizzes;
}