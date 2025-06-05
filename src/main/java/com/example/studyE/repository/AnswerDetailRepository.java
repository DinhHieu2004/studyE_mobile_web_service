package com.example.studyE.repository;


import com.example.studyE.entity.AnswerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerDetailRepository extends JpaRepository<AnswerDetail, Long> {

    @Query("SELECT ad.questionText FROM AnswerDetail ad WHERE ad.quizResult.userId = :userId AND ad.userAnswer IS NOT NULL AND ad.userAnswer <> ''")
    List<String> findAnsweredQuestionTextsByUserId(@Param("userId") long userId);



    @Query("""
    SELECT COUNT(ad), 
           SUM(CASE WHEN ad.correctAnswer = ad.userAnswer THEN 1 ELSE 0 END)
    FROM AnswerDetail ad
    WHERE ad.quizResult.userId = :userId
""")
    List<Object[]> getUserStats(@Param("userId") Long userId);


    @Query("""
    SELECT ad.category,
           COUNT(ad), 
           SUM(CASE WHEN ad.correctAnswer = ad.userAnswer THEN 1 ELSE 0 END)
    FROM AnswerDetail ad
    WHERE ad.quizResult.userId = :userId
    GROUP BY ad.category
""")
    List<Object[]> getStatsByCategory(@Param("userId") Long userId);

    @Query(value = """
    SELECT DATE(qr.timestamp), 
           COUNT(ad.id), 
           SUM(CASE WHEN ad.correct_answer = ad.user_answer THEN 1 ELSE 0 END)
    FROM answer_detail ad
    JOIN quiz_result qr ON ad.quiz_result_id = qr.id
    WHERE qr.user_id = :userId
    GROUP BY DATE(qr.timestamp)
    ORDER BY DATE(qr.timestamp)
""", nativeQuery = true)
    List<Object[]> getProgressByDate(@Param("userId") Long userId);


}