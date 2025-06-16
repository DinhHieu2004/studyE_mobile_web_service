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
                .map(v -> new VocabularyResponse(
                        v.getId(),
                        v.getWord(),
                        v.getMeaning(),
                        v.getExample(),
                        v.getAudioUrl(),
                        v.getImageUrl()
                ))
                .collect(Collectors.toList());
    }
}

