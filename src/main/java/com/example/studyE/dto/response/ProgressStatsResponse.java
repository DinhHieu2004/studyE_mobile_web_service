package com.example.studyE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressStatsResponse {
    LocalDate date;
    long totalQuestions;
    long correctAnswers;
    double accuracy;
}
