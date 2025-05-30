package com.example.studyE.repository;

import com.example.studyE.entity.Lession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LessionRepository extends JpaRepository<Lession, Long> {
    Page<Lession> findByTopicId(Long topicId, Pageable pageable);
}

