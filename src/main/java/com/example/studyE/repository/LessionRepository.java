package com.example.studyE.repository;

import com.example.studyE.entity.Lession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessionRepository extends JpaRepository<Lession, Long> {
    List<Lession> findByTopicId(Long topicId); // lấy bài học theo topic
}

