package com.example.studyE.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class LessionRequest {
    private String title;
    private String description;
    private String level;
    private String imageUrl;
    private String audioUrl;
    private Long topicId;
    private String status;

    @JsonAlias({"premium", "isPremium"})
    private Boolean premium;
}
