package com.example.studyE.repository;

import com.example.studyE.Entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;


@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {



    List<QuizResult> findAllByUserId(long userId);

    List<QuizResult> findAllByUserIdAndTimestampBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

}
