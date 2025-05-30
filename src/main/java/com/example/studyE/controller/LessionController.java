package com.example.studyE.controller;

import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.service.LessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessions")
@RequiredArgsConstructor
public class LessionController {

    private final LessionService lessionService;

    @GetMapping
    public ResponseEntity<PageResponse<LessionResponse>> getLessionsByTopic(
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<LessionResponse> response = lessionService.getLessionsByTopic(topicId, page, size);
        return ResponseEntity.ok(response);
    }
}
