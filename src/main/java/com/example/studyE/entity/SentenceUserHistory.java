package com.example.studyE.entity;

import com.example.studyE.entity.id.HistoryId;
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

public class SentenceUserHistory {
    @EmbeddedId
    HistoryId id;

    @Column(name = "answered_at")
    LocalDateTime answeredAt;


    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @MapsId("questionId")
    @JoinColumn(name = "question_id")
    private Sentence sentence;


}
