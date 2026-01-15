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
@Table(name = "user_watched_topic_v")
public class UserWatchedTopicV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @ManyToOne
    @JoinColumn(name = "topic_v_id")
    TopicVocabulary topic;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    @Column(name = "viewed_at")
    LocalDateTime viewedAt;
}
