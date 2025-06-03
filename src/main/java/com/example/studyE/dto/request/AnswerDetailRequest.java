package com.example.studyE.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AnswerDetailRequest {
    String questionText;
    List<String> options;
    String correctAnswer;
    String userAnswer;
    String category;

}
