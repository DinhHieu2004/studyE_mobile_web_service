package com.example.studyE.repository;

import com.example.studyE.entity.Lession;
import com.example.studyE.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface LessionRepository extends JpaRepository<Lession, Long> {
    Page<Lession> findAllByTopic(Topic topic, Pageable pageable);
}

