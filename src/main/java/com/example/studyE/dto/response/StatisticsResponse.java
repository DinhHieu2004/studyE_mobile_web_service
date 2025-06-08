package com.example.studyE.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticsResponse {
    long totalQuestions;
    long correctAnswers;
    double accuracyPercentage;
    List<CategoryStatsResponse> categoryStats;
    List<ProgressStatsResponse> progressStats;
}
