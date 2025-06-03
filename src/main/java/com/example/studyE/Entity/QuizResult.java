package com.example.studyE.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long userId;

    int score;

    int total;

    long duration;

    LocalDateTime timestamp;

    @OneToMany(mappedBy = "quizResult", cascade = CascadeType.ALL)
    private List<AnswerDetail> answers;


}

