package com.example.studyE.entity.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Embeddable
public class HistoryId implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "question_id")
    Long questionId;


}

