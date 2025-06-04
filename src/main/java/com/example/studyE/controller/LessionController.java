package com.example.studyE.controller;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.service.LessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessions")
@RequiredArgsConstructor
public class LessionController {

    private final LessionService lessionService;

    @GetMapping("/{id}")
    public ResponseEntity<LessionResponse> getLessionById(@PathVariable Long id) {
        LessionResponse response = lessionService.getLessionById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<LessionResponse> createLession(@RequestBody LessionRequest request) {
        LessionResponse response = lessionService.createLession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessionResponse> updateLession(
            @PathVariable Long id,
            @RequestBody LessionRequest request
    ) {
        LessionResponse response = lessionService.updateLession(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLession(@PathVariable Long id) {
        lessionService.deleteLession(id);
        return ResponseEntity.noContent().build();
    }


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
