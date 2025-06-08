package com.example.studyE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryStatsResponse {
    String categoryName;
    long totalQuestions;
    long correctAnswers;
    double accuracy;
}
