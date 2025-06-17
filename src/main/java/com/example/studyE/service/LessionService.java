package com.example.studyE.service;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;

import java.util.List;

public interface LessionService {
    LessionResponse getLessionById(Long id);

    LessionResponse createLession(LessionRequest request);

    LessionResponse updateLession(Long id, LessionRequest request);

    void deleteLession(Long id);

    PageResponse<LessionResponse> getLessionsByTopic(Long topicId, int page, int size);


    List<LessionResponse> getAllLessions();
// Lấy toàn bộ danh sách bài học, dùng cho admin hoặc dropdown chọn bài

    boolean existsById(Long id);

    List<LessionResponse> getLessonsWatched(Long userId);

    void markAsWatched(Long lessonId, Long userId);


}

