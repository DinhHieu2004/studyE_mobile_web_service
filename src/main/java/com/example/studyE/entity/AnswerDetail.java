package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class AnswerDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String questionText;

    @ElementCollection
    List<String> options;

    String correctAnswer;

    String userAnswer;

    String category;

    @ManyToOne
    @JoinColumn(name = "quiz_result_id")
    QuizResult quizResult;

}
