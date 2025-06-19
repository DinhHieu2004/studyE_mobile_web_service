package com.example.studyE.mapper;


import com.example.studyE.dto.response.SentenceResponse;
import com.example.studyE.entity.Sentence;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SentenceMapper {

     SentenceResponse toResponse(Sentence sentence);
}
