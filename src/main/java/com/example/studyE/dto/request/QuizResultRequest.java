package com.example.studyE.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class QuizResultRequest {
    int score;
    int total;
    long duration;
    LocalDateTime timestamp;
    List<AnswerDetailRequest> answers;
}
