package com.example.studyE.mapper;

import com.example.studyE.Entity.Question;
import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "content", source = "question")
    @Mapping(target = "correctAnswer", source = "correctAnswer")
    Question toEntity(OpenTriviaQuestionResponse dto);

}
