package com.example.studyE.service;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.dto.response.VocabularyResponse;

import java.util.List;

public interface VocabularyService {
    List<VocabularyResponse> getVocabularyByLessionId(Long lessionId);

    List<VocabularyResponse> getVocabularyReviewByLessonId(Long lessionId);

    List<VocabularyResponse> getVocabularyByTopicId(Long topicId);

    List<VocabularyResponse> getVocabularyReviewByTopicId(Long topicId);
}

