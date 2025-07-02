package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.*;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerSubmissionRepository extends JpaRepository<AnswerSubmission, Long> {
        List<AnswerSubmission> findBySelectedAnswer(Answer answer);
 @Query("SELECT new com.LinkVerse.identity.dto.response.QuestionStats(a.question.content, COUNT(a), SUM(CASE WHEN a.selectedAnswer.isCorrect = true THEN 1 ELSE 0 END)) " +
           "FROM AnswerSubmission a WHERE a.submission.quiz.id = :quizId GROUP BY a.question.id, a.question.content")
    List<QuestionStats> getStatsForQuiz(@Param("quizId") Long quizId);

}
