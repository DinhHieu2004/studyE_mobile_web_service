package com.example.studyE.service;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.dto.response.UnlockableVocabularyResponse;
import com.example.studyE.dto.response.VocabularyCardPreviewDTO;
import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.entity.VocabularyCard;

import java.util.List;

public interface VocabularyService {
    List<VocabularyResponse> getVocabularyByLessionId(Long lessionId);
    List<VocabularyCardPreviewDTO> getVocabularyAlls();

    List<VocabularyResponse> getVocabularyReviewByLessonId(Long lessionId);

    List<VocabularyResponse> getVocabularyByTopicId(Long topicId);

    List<VocabularyResponse> getVocabularyReviewByTopicId(Long topicId, Long uid);
    List<UnlockableVocabularyResponse> getVocabularyUnlockable(Long topicId, Long uid);
}

