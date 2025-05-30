package com.example.studyE.service.impl;

import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.entity.Lession;
import com.example.studyE.mapper.LessionMapper;
import com.example.studyE.repository.LessionRepository;
import com.example.studyE.service.LessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessionServiceImpl implements LessionService {

    private final LessionRepository lessionRepository;

    @Override
    public PageResponse<LessionResponse> getLessionsByTopic(Long topicId, int page, int size) {
        Page<Lession> lessionPage = lessionRepository.findByTopicId(topicId, PageRequest.of(page, size));
        List<LessionResponse> content = lessionPage.getContent()
                .stream()
                .map(LessionMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<LessionResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(lessionPage.getTotalPages())
                .totalItems(lessionPage.getTotalElements())
                .isLast(lessionPage.isLast())
                .items(content)
                .build();
    }
}
