package com.example.studyE.mapper;


import com.example.studyE.entity.AnswerDetail;
import com.example.studyE.entity.QuizResult;
import com.example.studyE.dto.request.AnswerDetailRequest;
import com.example.studyE.dto.request.QuizResultRequest;
import com.example.studyE.dto.response.QuizResultResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface QuizResultMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quizResult", ignore = true)
    AnswerDetail toEntity(AnswerDetailRequest dto);

    QuizResultResponse toQuizResultResponse(QuizResult quizResult);



    @Mapping(target = "id", ignore = true)
    QuizResult toEntity(QuizResultRequest dto);

}
