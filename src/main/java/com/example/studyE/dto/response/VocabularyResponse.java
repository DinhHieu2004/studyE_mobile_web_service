package com.example.studyE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyResponse {
    private Long id;
    private String word;
    private String meaning;
    private String example;
    private String audioUrl;
    private String imageUrl;
}

