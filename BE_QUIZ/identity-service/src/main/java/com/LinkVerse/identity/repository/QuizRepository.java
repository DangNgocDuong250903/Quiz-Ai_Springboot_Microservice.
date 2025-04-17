package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySubject(Subject subject);
}
