package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.Question;
import com.LinkVerse.identity.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);

}
