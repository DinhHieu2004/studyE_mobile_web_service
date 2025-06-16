package com.example.studyE.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressForPronounce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "sentence_id")
    Sentence sentence;

    @Column(length = 1000)
    String userSpeech;

    int score;

    String status;

    @JsonProperty("isCompleted")
    boolean isCompleted;


    long timestamp;

    int partId;

    @Override
    public String toString() {
        return "UserProgress{" +
                "id=" + id +
                ", user=" + user +
                ", sentence=" + sentence +
                ", userSpeech='" + userSpeech + '\'' +
                ", score=" + score +
                ", status='" + status + '\'' +
                ", isCompleted=" + isCompleted +
                ", timestamp=" + timestamp +
                ", partId=" + partId +
                '}';
    }


}
