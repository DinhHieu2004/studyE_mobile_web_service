package com.example.studyE.service;

import com.example.studyE.dto.response.UnlockQuestionDTO;
import com.example.studyE.dto.response.UnlockableVocabularyResponse;
import com.example.studyE.entity.UnlockQuestion;
import com.example.studyE.entity.UnlockQuestionOption;
import com.example.studyE.entity.UserVocabularyUnlock;
import com.example.studyE.entity.VocabularyCard;
import com.example.studyE.repository.UnlockQuestionOptionRepository;
import com.example.studyE.repository.UnlockQuestionRepository;
import com.example.studyE.repository.UserVocabularyUnlockRepository;
import com.example.studyE.repository.VocabularyCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnlockQuestionService {

    private final UnlockQuestionRepository questionRepo;
    private final UnlockQuestionOptionRepository optionRepo;
    private final UserVocabularyUnlockRepository unlockRepo;
    private final VocabularyCardRepository vocabularyRepo;

    public UnlockQuestionDTO getUnlockQuestion(Long userId, Long cardId) {

        LocalDateTime now = LocalDateTime.now();

        VocabularyCard vocabulary = vocabularyRepo.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));

        UserVocabularyUnlock unlock = unlockRepo
                .findByUserIdAndVocabulary_Id(userId, cardId)
                .orElseGet(() -> unlockRepo.save(
                        UserVocabularyUnlock.builder()
                                .userId(userId)
                                .vocabulary(vocabulary)
                                .remainingAttempts(3)
                                .unlocked(false)
                                .build()
                ));


        if (unlock.getLockedUntil() != null &&
                unlock.getLockedUntil().isBefore(now)) {

            unlock.setRemainingAttempts(3);
            unlock.setLockedUntil(null);
            unlockRepo.save(unlock);
        }

        if (unlock.getLockedUntil() != null &&
                unlock.getLockedUntil().isAfter(now)) {

            String timeText = unlock.getLockedUntil()
                    .toString()
                    .replace('T', ' ')
                    .substring(0, 16);

            return new UnlockQuestionDTO(
                    0L,
                    "Bạn đã hết lượt. Vui lòng quay lại sau lúc " + timeText,
                    List.of()
            );
        }

        UnlockQuestion q = questionRepo.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Unlock question not found"));

        List<String> options = optionRepo
                .findByQuestionIdOrderByOptionIndex(q.getId())
                .stream()
                .map(UnlockQuestionOption::getOptionText)
                .toList();

        return new UnlockQuestionDTO(
                q.getId(),
                q.getQuestion(),
                options
        );
    }


    @Transactional
    public UnlockableVocabularyResponse confirmUnlock(
            Long userId,
            Long questionId,
            Integer answerIndex
    ) {
        LocalDateTime now = LocalDateTime.now();

        UnlockQuestion q = questionRepo.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        VocabularyCard vocabulary = vocabularyRepo.findById(q.getCardId())
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));

        UserVocabularyUnlock unlock = unlockRepo
                .findByUserIdAndVocabulary_Id(userId, vocabulary.getId())
                .orElseThrow(() -> new RuntimeException("Unlock state not found"));

        if (unlock.getLockedUntil() != null &&
                unlock.getLockedUntil().isAfter(now)) {
            throw new RuntimeException("Vocabulary is locked");
        }

        if (q.getCorrectIndex().equals(answerIndex)) {
            unlock.setUnlocked(true);
            unlock.setUnlockedAt(now);
        } else {
            int remain = unlock.getRemainingAttempts() - 1;
            unlock.setRemainingAttempts(remain);

            // 🔒 HẾT LƯỢT → KHÓA
            if (remain <= 0) {
                unlock.setLockedUntil(now.plusHours(6)); // hoặc minutes
            }
        }

        unlockRepo.save(unlock);

        UnlockableVocabularyResponse.UnlockableVocabularyResponseBuilder builder =
                UnlockableVocabularyResponse.builder()
                        .id(vocabulary.getId())
                        .unlocked(unlock.isUnlocked())
                        .remainingAttempts(unlock.getRemainingAttempts())
                        .lockedUntil(unlock.getLockedUntil());

        if (unlock.isUnlocked()) {
            builder
                    .word(vocabulary.getWord())
                    .phonetic(vocabulary.getPhonetic())
                    .imageUrl(vocabulary.getImageUrl());
        }

        return builder.build();
    }

}

