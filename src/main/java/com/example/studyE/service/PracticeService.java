package com.example.studyE.service;

import com.example.studyE.entity.PartProgress;
import com.example.studyE.entity.Sentence;
import com.example.studyE.entity.User;
import com.example.studyE.entity.UserProgress;
import com.example.studyE.repository.PartProgressRepository;
import com.example.studyE.repository.SentenceRepository;
import com.example.studyE.repository.UserProgressRepository;
import com.example.studyE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PracticeService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private PartProgressRepository partProgressRepository;

    @Autowired
    private SentenceRepository sentenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void savePracticeResult(String userUid, UserProgress progress) {
        User user = userRepository.findByUid(userUid)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Sentence sentence = sentenceRepository.findById((long) progress.getSentence().getId())
                .orElseThrow(() -> new RuntimeException("Sentence not found"));

        UserProgress userProgress = UserProgress.builder()
                .user(user)
                .partId(progress.getPartId())
                .sentence(sentence)
                .sentenceContent(progress.getSentenceContent())
                .userSpeech(progress.getUserSpeech())
                .score(progress.getScore())
                .status(progress.getStatus())
                .isCompleted(progress.getIsCompleted())
                .timestamp(progress.getTimestamp())
                .build();

        userProgressRepository.save(userProgress);
    }

    public PartProgress getPartProgress(String userUid, Integer partId) {
        Optional<PartProgress> partProgressOpt = partProgressRepository.findByUserUidAndPartId(userUid, partId);
        List<Sentence> sentences = sentenceRepository.findByPartNumber(partId, null);
        int totalSentences = sentences.size();
        int completedSentences = userProgressRepository.findByUserUidAndPartIdAndIsCompletedTrue(userUid, partId).size();
        double completionPercentage = totalSentences > 0 ? (completedSentences * 100.0 / totalSentences) : 0;

        PartProgress partProgress;
        if (partProgressOpt.isPresent()) {
            partProgress = partProgressOpt.get();
            partProgress.setTotalSentences(totalSentences);
            partProgress.setCompletedSentences(completedSentences);
            partProgress.setCompletionPercentage(completionPercentage);
            partProgress.setLastUpdated(Instant.now());
        } else {
            partProgress = PartProgress.builder()
                    .user(userRepository.findByUid(userUid).orElseThrow(() -> new RuntimeException("User not found")))
                    .partId(partId)
                    .partName("Part " + partId)
                    .totalSentences(totalSentences)
                    .completedSentences(completedSentences)
                    .completionPercentage(completionPercentage)
                    .lastUpdated(Instant.now())
                    .build();
        }
        partProgressRepository.save(partProgress);

        return
               /**
                new PartProgress(
                partProgress.getPartId(),
                partProgress.getPartName(),
                partProgress.getTotalSentences(),
                partProgress.getCompletedSentences(),
                partProgress.getCompletionPercentage(),
                partProgress.getLastUpdated().toEpochMilli()
        )   **/
              PartProgress.builder()
                      .id(partProgress.getId())
                      .partName(partProgress.getPartName())
                      .totalSentences(partProgress.getTotalSentences())
                      .completedSentences(partProgress.getCompletedSentences())
                      .completionPercentage(partProgress.getCompletionPercentage())
                      .lastUpdated(partProgress.getLastUpdated())

                      .build();
    }

    public List<Sentence> getIncompleteSentences(String userUid, Integer partId) {
        List<Sentence> allSentences = sentenceRepository.findByPartNumber(partId, null);
        List<Long> completedSentenceIds = userProgressRepository
                .findByUserUidAndPartIdAndIsCompletedTrue(userUid, partId)
                .stream()
                .map(up -> up.getSentence().getId())
                .collect(Collectors.toList());

        return allSentences.stream()
                .filter(s -> !completedSentenceIds.contains(s.getId()))
                .map(s ->
                                //Sentence(s.getId().intValue(), s.getContent())
                Sentence.builder().
                        id(s.getId()).
                        content(s.getContent()).
                        build()

                )
                .collect(Collectors.toList());
    }

    @Transactional
    public PartProgress updatePartProgress(String userUid, Map<String, Object> progressData) {
        Integer partId = ((Number) progressData.get("part")).intValue();
        return getPartProgress(userUid, partId);
    }
}