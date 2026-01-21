package com.example.studyE.repository;

import com.example.studyE.entity.UserVocabularyUnlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserVocabularyUnlockRepository extends JpaRepository<UserVocabularyUnlock, Long> {
    @Query("""
select u from UserVocabularyUnlock u
where u.userId = :userId
and u.vocabulary.id in :vocabIds
""")
    List<UserVocabularyUnlock> findByUserIdAndVocabularyId(
            Long userId,
            List<Long> vocabIds
    );
    Optional<UserVocabularyUnlock> findByUserIdAndVocabulary_Id(
            Long userId,
            Long vocabularyId
    );
    @Query("""
        SELECT u
        FROM UserVocabularyUnlock u
        JOIN FETCH u.vocabulary v
        WHERE u.userId = :userId
          AND v.topic.id = :topicId
          AND u.unlocked = true
    """)
    List<UserVocabularyUnlock> findUnlockedByUserAndTopic(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId
    );
}


