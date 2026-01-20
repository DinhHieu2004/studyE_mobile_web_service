package com.example.studyE.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnlockConfirmRequest {
    Long questionId;
    Integer answerIndex;
}
