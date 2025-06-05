package com.example.studyE.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizResultResponse {

    int score;

    int total;

    long duration;

    LocalDateTime timestamp;

    List<AnswerDetailResponse> answers;
}
