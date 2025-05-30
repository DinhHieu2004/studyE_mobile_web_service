package com.example.studyE.service;

import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;

public interface LessionService {
    PageResponse<LessionResponse> getLessionsByTopic(Long topicId, int page, int size);
}

