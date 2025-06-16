package com.example.studyE.repository;

import com.example.studyE.entity.UserProgressForPronounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgressForPronounceRepository extends JpaRepository<UserProgressForPronounce, Long> {

    boolean existsByUser_IdAndSentence_Id(Long userId, Long sentenceId);

    long countByUser_IdAndPartIdAndSentence_LevelAndIsCompletedTrue(Long userId, int partId, String level);

}
