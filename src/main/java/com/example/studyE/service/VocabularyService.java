package com.example.studyE.service;

import com.example.studyE.dto.response.VocabularyResponse;

import java.util.List;

public interface VocabularyService {
    List<VocabularyResponse> getVocabularyByLessionId(Long lessionId);

    List<VocabularyResponse> getVocabularyReviewByLessonId(Long lessionId);
}

