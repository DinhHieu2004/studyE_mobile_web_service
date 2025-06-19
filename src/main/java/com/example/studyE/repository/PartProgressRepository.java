package com.example.studyE.repository;


import com.example.studyE.entity.PartProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartProgressRepository extends JpaRepository<PartProgress, Long> {
    Optional<PartProgress> findByUserUidAndPartId(String userUid, Integer partId);
}