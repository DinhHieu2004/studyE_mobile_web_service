package com.example.studyE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessionResponse {
    private Long id;
    private String title;
    private String description;
    private String level;
    private String imageUrl;
    private String audioUrl;
    private Long topicId;
    private String topicName;
    private String status;
    private String progressStatus;
    private LocalDateTime completedAt;
    private boolean isPremium;

}

