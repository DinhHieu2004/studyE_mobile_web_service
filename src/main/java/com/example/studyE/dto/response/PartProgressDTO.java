package com.example.studyE.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartProgressDTO {
    int partId;
    String partName;
    int totalSentences;
    int completedSentences;
    double completionPercentage;
    long lastUpdated;
}
