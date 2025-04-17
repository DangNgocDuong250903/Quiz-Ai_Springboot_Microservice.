package com.LinkVerse.identity.repository;

import com.LinkVerse.identity.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerSubmissionRepository extends JpaRepository<AnswerSubmission, Long> {
        List<AnswerSubmission> findBySelectedAnswer(Answer answer);


}
