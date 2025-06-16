package com.example.studyE.service;

import com.example.studyE.dto.response.PartProgressDTO;
import com.example.studyE.entity.*;
import com.example.studyE.repository.*;
import com.example.studyE.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PracticeService {


    PartProgressRepository partProgressRepository;

    SentenceRepository sentenceRepository;

    UserRepository userRepository;

    UserProgressForPronounceRepository userProgressForPronounceRepository;


    public void savePracticeResult(UserProgressForPronounce progress) {
        System.out.println(progress);

        Long userId = JwtUtil.getUserIdFromToken();
        User user = userRepository.findById(userId).orElseThrow();
        Sentence sentence = sentenceRepository.findById(progress.getSentence().getId()).orElseThrow();

        progress.setUser(user);
        progress.setSentence(sentence);
        userProgressForPronounceRepository.save(progress);
    }

    public PartProgressDTO getPartProgress( int part,String level) {
        Long userId = JwtUtil.getUserIdFromToken();
        List<Sentence> allSentences = sentenceRepository.findByPartNumberAndLevel(part, level);
        long total = allSentences.size();
        long completed = userProgressForPronounceRepository.countByUser_IdAndPartIdAndSentence_LevelAndIsCompletedTrue(userId, part, level);

        double percent = total == 0 ? 0 : ((double) completed / total) * 100;

        return PartProgressDTO.builder()
                .partNumber(part)
                .totalSentences((int) total)
                .completedSentences((int) completed)
                .completionPercentage(percent)
                .lastUpdated(System.currentTimeMillis())
                .build();
    }

    public List<Sentence> getIncompleteSentences(int partId, String level) {
        Long userId = JwtUtil.getUserIdFromToken();
        List<Sentence> all = sentenceRepository.findByPartNumberAndLevel(partId, level);

        return all.stream()
                .filter(s -> !userProgressForPronounceRepository.existsByUser_IdAndSentence_Id(userId, s.getId()))
                .peek(s -> {
                    String context = s.getContent();
                    if (context != null && context.length() > 3) {
                        s.setContent(cutString(3, context));
                    }
                })
                .toList();
    }


    public String cutString(int amount, String text){
        return text.substring(amount);
    }


}