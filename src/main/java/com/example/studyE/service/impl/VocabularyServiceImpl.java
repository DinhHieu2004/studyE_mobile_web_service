package com.example.studyE.service.impl;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.repository.TopicVocabularyRepository;
import com.example.studyE.repository.VocabularyCardRepository;

import com.example.studyE.entity.Dialog;
import com.example.studyE.repository.DialogRepository;

import com.example.studyE.repository.VocabularyRepository;
import com.example.studyE.service.VocabularyService;
import com.example.studyE.util.DictionaryClient;
import com.example.studyE.util.MyMemoryTranslateClient;
import com.example.studyE.util.WordExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyCardRepository vocabularyCardRepository;

    private final DialogRepository dialogRepository;
    private final DictionaryClient dictionaryClient;
    private final MyMemoryTranslateClient translateClient;


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
    public List<VocabularyResponse> getVocabularyReviewByLessonId(Long lessonId) {

        List<String> sentences = dialogRepository.findByLessionId(lessonId)
                .stream().map(Dialog::getContent).toList();

        List<String> words = WordExtractor.topWords(sentences, 10);

        return words.stream().map(w -> {
            var dict = dictionaryClient.lookup(w);

            String exampleEn = WordExtractor.pickExample(sentences, w);
            String meaningVi = translateClient.enToVi(dict.definitionEn());
            String exampleVi = "";

            long stableId = Math.abs((long) w.hashCode());

            return VocabularyResponse.builder()
                    .id(stableId)
                    .word(w)
                    .phonetic(dict.phonetic())
                    .audioUrl(dict.audioUrl())
                    .meaning(meaningVi.isBlank() ? dict.definitionEn() : meaningVi)
                    .example(exampleEn)
                    .exampleMeaning(exampleVi)
                    .build();
        }).toList();
    }

    @Override
    public List<VocabularyResponse> getVocabularyByTopicId(Long topicId) {
        return vocabularyCardRepository.findByTopicId(topicId).stream()
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
    public List<VocabularyResponse> getVocabularyReviewByTopicId(Long topicId) {
        return vocabularyCardRepository.findByTopicId(topicId).stream()
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

