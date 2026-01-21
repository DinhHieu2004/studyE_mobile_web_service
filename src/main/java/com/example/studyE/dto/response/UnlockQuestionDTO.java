package com.example.studyE.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnlockQuestionDTO {

    private Long questionId;
    private String question;
    private List<String> options;
}

