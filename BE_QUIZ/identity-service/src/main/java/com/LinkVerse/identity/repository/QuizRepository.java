package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.entity.Subject;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBySubject(Subject subject);

    @Query("""
        SELECT q FROM Quiz q
        LEFT JOIN FETCH q.subject
        LEFT JOIN FETCH q.questions qu
        LEFT JOIN FETCH qu.answers
        WHERE q.id = :id
    """)
    Optional<Quiz> findQuizWithQuestionsAndAnswers(@Param("id") Long id);
}
