package com.example.studyE.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VocabularyUpdateRequest {

    private Long topicId;
    private String word;
    private String meaning;
    private String phonetic;
    private String example;
    private String exampleMeaning;
    private String imageUrl;
    private String audioUrl;

    private String questionType;

    private ChoiceQuestion choice;
    private FillQuestion fill;

    @Data
    public static class ChoiceQuestion {
        private String question;

        @JsonProperty("A")
        private String a;

        @JsonProperty("B")
        private String b;

        @JsonProperty("C")
        private String c;

        @JsonProperty("D")
        private String d;

        private String correct;
    }


    @Data
    public static class FillQuestion {
        private String question;
        private String answer;
    }
}
