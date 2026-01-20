package com.example.studyE.repository;

import com.example.studyE.entity.UnlockQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UnlockQuestionRepository
        extends JpaRepository<UnlockQuestion, Long> {

    Optional<UnlockQuestion> findByCardId(Long cardId);
}

