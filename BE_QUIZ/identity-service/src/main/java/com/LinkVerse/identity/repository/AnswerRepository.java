package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.Answer;
import com.LinkVerse.identity.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestion(Question question);

}
