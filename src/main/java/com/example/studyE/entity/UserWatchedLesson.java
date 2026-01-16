package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
        name = "user_watched_lesson",
        uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "user_id"})
)
public class UserWatchedLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    Lession lesson;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "viewed_at")
    LocalDateTime viewedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    ProgressStatus status;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
