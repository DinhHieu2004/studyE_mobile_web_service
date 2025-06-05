package com.example.studyE.dto.response;

import jakarta.persistence.ElementCollection;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDetailResponse {
    String questionText;

    List<String> options;

    String correctAnswer;

    String userAnswer;

    String category;
}
