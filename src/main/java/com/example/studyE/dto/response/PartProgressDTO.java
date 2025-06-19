package com.example.studyE.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartProgressDTO {
    int partNumber;
    int totalSentences;
    int completedSentences;
    double completionPercentage;
    long lastUpdated;
}
