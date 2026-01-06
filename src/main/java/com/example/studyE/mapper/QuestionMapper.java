package com.example.studyE.mapper;

import com.example.studyE.entity.Question;
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

    @Mapping(target = "id", source = "id")
    @Mapping(target = "question", source = "content")
    @Mapping(target = "correctAnswer", source = "correctAnswer")
    OpenTriviaQuestionResponse toResponse(Question question);

}
