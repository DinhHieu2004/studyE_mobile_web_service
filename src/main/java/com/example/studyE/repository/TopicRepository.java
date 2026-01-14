package com.example.studyE.repository;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("""
        SELECT new com.example.studyE.dto.response.TopicHomeResponse(
            t.id,
            t.name,
            t.imagePath
        )
        FROM Topic t
    """)
    List<TopicHomeResponse> findTopicsForHome();
}
