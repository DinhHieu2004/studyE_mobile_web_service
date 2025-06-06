package com.example.studyE.mapper;

import com.example.studyE.entity.AnswerDetail;
import com.example.studyE.dto.response.AnswerDetailResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerDetailMapper {

    AnswerDetailResponse toAnswerDetailResponse(AnswerDetail answerDetail);


}
