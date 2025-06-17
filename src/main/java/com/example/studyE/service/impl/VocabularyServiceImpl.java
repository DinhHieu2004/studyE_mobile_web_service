package com.example.studyE.service.impl;

import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.repository.VocabularyRepository;
import com.example.studyE.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    @Override
    public List<VocabularyResponse> getVocabularyByLessionId(Long lessionId) {
        return vocabularyRepository.findByLessionId(lessionId).stream()
                .map(v -> VocabularyResponse.builder()
                        .id(v.getId())
                        .word(v.getWord())
                        .imageUrl(v.getImageUrl())
                        .audioUrl(v.getAudioUrl())
                        .meaning(v.getMeaning())
                        .example(v.getExample())
                        .build())
                .toList();
    }
    @Override
    public List<VocabularyResponse> getVocabularyReviewByLessonId(Long lessionId) {
        return vocabularyRepository.findByLessionId(lessionId).stream()
                .map(vocab -> VocabularyResponse.builder()
                        .id(vocab.getId())
                        .word(vocab.getWord())
                        .phonetic(vocab.getPhonetic())
                        .imageUrl(vocab.getImageUrl())
                        .audioUrl(vocab.getAudioUrl())
                        .meaning(vocab.getMeaning())
                        .example(vocab.getExample())
                        .exampleMeaning(vocab.getExampleMeaning())
                        .build())
                .toList();
    }
}

