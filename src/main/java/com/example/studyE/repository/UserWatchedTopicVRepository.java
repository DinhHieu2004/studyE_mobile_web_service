package com.example.studyE.repository;

import com.example.studyE.entity.TopicVocabulary;
import com.example.studyE.entity.User;
import com.example.studyE.entity.UserWatchedTopicV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserWatchedTopicVRepository extends JpaRepository<UserWatchedTopicV, Long> {
    List<UserWatchedTopicV> findByUser(User user);

    @Query("""
    SELECT uw.topic
    FROM UserWatchedTopicV uw
    WHERE uw.user.id = :userId
""")
    List<TopicVocabulary> findWatchedTopicVsByUserId(@Param("userId") Long userId);

    boolean existsByTopic_IdAndUser_Id(Long topicId, Long userId);

}
