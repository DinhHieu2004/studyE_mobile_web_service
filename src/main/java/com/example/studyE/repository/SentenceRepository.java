package com.example.studyE.repository;

import com.example.studyE.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    boolean existsByContentAndLevel(String content, String level);
}