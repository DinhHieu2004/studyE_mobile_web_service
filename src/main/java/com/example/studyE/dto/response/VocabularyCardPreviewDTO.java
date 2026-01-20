package com.example.studyE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VocabularyCardPreviewDTO {

    private int row;
    private Long id;
    private String word;
    private Long topicId;
    private String meaning;
    private String phonetic;
    private String example;
    private String exampleMeaning;
    private String imageUrl;
    private String audioUrl;
}
