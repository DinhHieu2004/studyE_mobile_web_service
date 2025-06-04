package com.example.studyE.service;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;

public interface LessionService {
    LessionResponse getLessionById(Long id);

    LessionResponse createLession(LessionRequest request);

    LessionResponse updateLession(Long id, LessionRequest request);

    void deleteLession(Long id);

    PageResponse<LessionResponse> getLessionsByTopic(Long topicId, int page, int size);
}

