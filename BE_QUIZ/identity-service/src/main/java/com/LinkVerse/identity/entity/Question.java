package com.LinkVerse.identity.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String content;

    @ManyToOne
    Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    List<Answer> answers;
}
