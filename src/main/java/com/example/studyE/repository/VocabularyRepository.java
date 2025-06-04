package com.example.studyE.repository;

import com.example.studyE.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    List<Vocabulary> findByLessionId(Long lessionId);
}

