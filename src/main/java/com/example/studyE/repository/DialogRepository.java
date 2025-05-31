package com.example.studyE.repository;

import com.example.studyE.entity.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DialogRepository extends JpaRepository<Dialog, Long> {
    List<Dialog> findByLessionId(Long lessionId);
}

