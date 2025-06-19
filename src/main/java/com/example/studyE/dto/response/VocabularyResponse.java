package com.example.studyE.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabularyResponse {
    Long id;
    String word;
    String meaning;
    String example;
    String audioUrl;
    String imageUrl;
    String exampleMeaning;
    String phonetic;
}


