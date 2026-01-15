package com.example.studyE.repository;

import com.example.studyE.entity.VocabularyCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabularyCardRepository extends JpaRepository<VocabularyCard, Long> {
    List<VocabularyCard> findByTopicId(Long topicId);
}


