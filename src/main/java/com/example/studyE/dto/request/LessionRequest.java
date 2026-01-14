package com.example.studyE.dto.request;

import lombok.Data;

@Data
public class LessionRequest {
    private String title;
    private String description;
    private String level;
    private String imageUrl;
    private Long topicId;
    private String status;
}
