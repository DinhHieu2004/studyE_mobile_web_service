package com.example.studyE.repository;

import com.example.studyE.Entity.Question;
import com.example.studyE.Entity.UserQuestionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserQuestionHistoryRepository extends JpaRepository<UserQuestionHistory, Long> {
    boolean existsByUser_UsernameAndQuestion(String username, Question question);


    @Query("SELECT h.question.id FROM UserQuestionHistory h WHERE h.user.id = :userId")
    List<Long> findQuestionIdsByUserId(Long userId);
}
