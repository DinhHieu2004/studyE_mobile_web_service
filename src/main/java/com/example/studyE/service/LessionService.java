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

    List<LessionResponse> searchLessionsByTitle(String keyword);
// Tìm kiếm bài học theo tiêu đề

    List<LessionResponse> getRecentLessions(int limit);
// Lấy các bài học mới nhất (ví dụ: dùng cho trang chủ hoặc dashboard)

    PageResponse<LessionResponse> getLessionsByTopicAndKeyword(Long topicId, String keyword, int page, int size);
// Kết hợp phân trang và tìm kiếm

    boolean existsById(Long id);
// Kiểm tra bài học có tồn tại không (hữu ích khi validate)

    long countLessionsByTopic(Long topicId);
// Đếm số bài học thuộc một chủ đề cụ thể (thống kê)

}

