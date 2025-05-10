package com.example.studyE.repository;

import com.example.studyE.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    boolean existsByContent(String content);

    Optional<Question> findByContent(String questionContent);
}

