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
@RequestMapping("/api/admin/lessions")
@RequiredArgsConstructor
public class AdminLessionController {

    private final LessionService lessionService;

    @GetMapping
    public ResponseEntity<PageResponse<LessionResponse>> getLessionsForAdmin(
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean premium,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(
                lessionService.getLessionsForAdmin(topicId, q, status, premium, level, page, size)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessionResponse> getLessionByIdForAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(lessionService.getLessionByIdForAdmin(id));
    }

    @PostMapping
    public ResponseEntity<LessionResponse> createLession(@RequestBody LessionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessionService.createLession(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessionResponse> updateLession(@PathVariable Long id, @RequestBody LessionRequest request) {
        return ResponseEntity.ok(lessionService.updateLession(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLession(@PathVariable Long id) {
        lessionService.deleteLession(id);
        return ResponseEntity.noContent().build();
    }
}
