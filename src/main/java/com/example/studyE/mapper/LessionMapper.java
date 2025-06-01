package com.example.studyE.mapper;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.entity.Lession;

public class LessionMapper {

    public static LessionResponse toDto(Lession lession) {
        return LessionResponse.builder()
                .id(lession.getId())
                .title(lession.getTitle())
                .description(lession.getDescription())
                .level(lession.getLevel())
                .imageUrl(lession.getImageUrl())
                .topicId(lession.getTopic().getId())
                .topicName(lession.getTopic().getName())
                .build();
    }

    public static Lession toEntity(LessionRequest request) {
        Lession lession = new Lession();
        lession.setTitle(request.getTitle());
        lession.setDescription(request.getDescription());
        lession.setLevel(request.getLevel());
        lession.setImageUrl(request.getImageUrl());

        return lession;
    }
}
