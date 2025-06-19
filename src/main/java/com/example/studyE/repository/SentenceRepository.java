package com.example.studyE.repository;

import com.example.studyE.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

    boolean existsByContentAndLevel(String content, String level);

    List<Sentence> findByLevelOrderById(String level);

    List<Sentence> findByLevel(String level);
    List<Sentence> findAllByLevel(String level);

    List<Sentence> findAllByLevelAndPartNumber(String level, Integer part);


   // List<Sentence> findByPartNumber(Integer partId, Object o);

    List<Sentence> findByPartNumber(int partNumber);

    List<Sentence> findByPartNumberAndLevel(int part, String level);
}