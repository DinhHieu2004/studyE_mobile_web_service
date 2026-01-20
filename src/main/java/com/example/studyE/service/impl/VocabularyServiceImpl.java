package com.example.studyE.service.impl;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.dto.response.UnlockableVocabularyResponse;
import com.example.studyE.dto.response.VocabularyCardPreviewDTO;
import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.entity.UserVocabularyUnlock;
import com.example.studyE.entity.VocabularyCard;
import com.example.studyE.repository.*;

import com.example.studyE.entity.Dialog;

import com.example.studyE.service.VocabularyService;
import com.example.studyE.util.DictionaryClient;
import com.example.studyE.util.MyMemoryTranslateClient;
import com.example.studyE.util.WordExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabularyServiceImpl implements VocabularyService {

    private final VocabularyRepository vocabularyRepository;

    private final VocabularyCardRepository vocabularyCardRepository;
    private final UserVocabularyUnlockRepository userVocabularyUnlockRepository;

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
    public List<VocabularyCardPreviewDTO> getVocabularyAlls() {

        return vocabularyCardRepository.findAll()
                .stream()
                .map(e -> {
                    VocabularyCardPreviewDTO dto = new VocabularyCardPreviewDTO();
                    dto.setId(e.getId());
                    dto.setWord(e.getWord());
                    dto.setMeaning(e.getMeaning());
                    dto.setPhonetic(e.getPhonetic());
                    dto.setExample(e.getExample());
                    dto.setExampleMeaning(e.getExampleMeaning());
                    dto.setImageUrl(e.getImageUrl());
                    dto.setAudioUrl(e.getAudioUrl());

                    if (e.getTopic() != null) {
                        dto.setTopicId(e.getTopic().getId());
                    }
                    return dto;
                })
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
    public List<VocabularyResponse> getVocabularyReviewByTopicId(
            Long topicId,
            Long uid
    ) {
        return userVocabularyUnlockRepository
                .findUnlockedByUserAndTopic(uid, topicId)
                .stream()
                .map(uv -> {
                    VocabularyCard v = uv.getVocabulary();
                    return VocabularyResponse.builder()
                            .id(v.getId())
                            .word(v.getWord())
                            .phonetic(v.getPhonetic())
                            .imageUrl(v.getImageUrl())
                            .audioUrl(v.getAudioUrl())
                            .meaning(v.getMeaning())
                            .example(v.getExample())
                            .exampleMeaning(v.getExampleMeaning())
                            .build();
                })
                .toList();
    }
    @Override
    public List<UnlockableVocabularyResponse> getVocabularyUnlockable(
            Long topicId,
            Long userId
    ) {
        List<VocabularyCard> vocabularies =
                vocabularyCardRepository.findByTopicId(topicId);

        if (vocabularies.isEmpty()) {
            return List.of();
        }

        List<Long> vocabIds = vocabularies.stream()
                .map(VocabularyCard::getId)
                .toList();

        List<UserVocabularyUnlock> unlocks =
                userVocabularyUnlockRepository
                        .findByUserIdAndVocabularyId(userId, vocabIds);

        Map<Long, UserVocabularyUnlock> unlockMap =
                unlocks.stream()
                        .collect(Collectors.toMap(
                                u -> u.getVocabulary().getId(),
                                u -> u
                        ));

        return vocabularies.stream().map(vocab -> {

            UserVocabularyUnlock unlock = unlockMap.get(vocab.getId());

            if (unlock == null || !unlock.isUnlocked()) {
                return UnlockableVocabularyResponse.builder()
                        .id(vocab.getId())
                        .unlocked(false)
                        .remainingAttempts(
                                unlock != null ? unlock.getRemainingAttempts() : 3
                        )
                        .lockedUntil(
                                unlock != null ? unlock.getLockedUntil() : null
                        )
                        .build();
            }

            return UnlockableVocabularyResponse.builder()
                    .id(vocab.getId())
                    .unlocked(true)
                    .word(vocab.getWord())
                    .phonetic(vocab.getPhonetic())
                    .imageUrl(vocab.getImageUrl())
                    .build();

        }).toList();
    }

}

