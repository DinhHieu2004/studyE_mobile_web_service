package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_vocabulary_unlock")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabularyUnlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "user_id", nullable = false)
    Long userId;
    @ManyToOne
    @JoinColumn(name = "vocabulary_id", nullable = false)
    VocabularyCard vocabulary;

    @Column(nullable = false)
    boolean unlocked;

    @Column(name = "remaining_attempts")
    Integer remainingAttempts;

    @Column(name = "locked_until")
    LocalDateTime lockedUntil;

    @Column(name = "unlocked_at")
    LocalDateTime unlockedAt;
}

