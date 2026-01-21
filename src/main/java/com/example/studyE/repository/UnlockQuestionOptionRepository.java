package com.example.studyE.repository;

import com.example.studyE.entity.UnlockQuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnlockQuestionOptionRepository
        extends JpaRepository<UnlockQuestionOption, Long> {

    List<UnlockQuestionOption> findByQuestionIdOrderByOptionIndex(Long questionId);
}

