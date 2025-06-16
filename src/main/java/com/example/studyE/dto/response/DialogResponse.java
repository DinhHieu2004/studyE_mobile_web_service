package com.example.studyE.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DialogResponse {
    private String speaker;
    private String content;
    private String audioUrl;
}
