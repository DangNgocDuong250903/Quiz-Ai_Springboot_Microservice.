package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.Quiz;
import com.LinkVerse.identity.entity.QuizSubmission;
import com.LinkVerse.identity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {
    List<QuizSubmission> findByUser(User user);
    List<QuizSubmission> findByQuiz(Quiz quiz);
    int countByQuizIdAndStartedAtIsNotNull(Long quizId);


}
