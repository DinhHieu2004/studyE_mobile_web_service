package com.example.studyE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnlockableVocabularyResponse {

    Long id;

    Boolean unlocked;
    Integer remainingAttempts;
    LocalDateTime unlockAt;
    LocalDateTime lockedUntil;

    String word;
    String phonetic;
    String imageUrl;
}

