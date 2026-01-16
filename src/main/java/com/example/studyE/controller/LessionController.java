package com.example.studyE.controller;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.service.LessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/lessions")
@RequiredArgsConstructor
public class LessionController {

    private final LessionService lessionService;

    @GetMapping("/watched")
    public ResponseEntity<List<LessionResponse>> getLessonsWatchedByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?,?>)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        Long userId = Long.valueOf(principal.get("userId").toString());

        List<LessionResponse> lessons = lessionService.getLessonsWatched(userId);
        return ResponseEntity.ok(lessons);
    }
    @PostMapping("/watched")
    public ResponseEntity<Void> markLessonWatched(@RequestParam("lessonId") Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?,?>)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        Long userId = Long.valueOf(principal.get("userId").toString());
        lessionService.markAsWatched(lessonId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/done")
    public ResponseEntity<Void> markLessonDone(@RequestParam("lessonId") Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?,?>)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        Long userId = Long.valueOf(principal.get("userId").toString());

        lessionService.markAsDone(lessonId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history/done")
    public ResponseEntity<List<LessionResponse>> getDoneHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?,?>)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();
        Long userId = Long.valueOf(principal.get("userId").toString());

        return ResponseEntity.ok(lessionService.getLessonsDoneHistory(userId));
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
    public ResponseEntity<PageResponse<LessionResponse>> getLessions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof Map<?, ?> principal) {
            userId = Long.valueOf(principal.get("userId").toString());
        }
        System.out.println("HIT getLessions userId=" + userId);

        return ResponseEntity.ok(lessionService.getLessions(topicId, page, size, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessionResponse> getLessionById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof Map<?, ?> principal) {
            userId = Long.valueOf(principal.get("userId").toString());
        }

        System.out.println("HIT getLessionById id=" + id + " userId=" + userId);

        return ResponseEntity.ok(lessionService.getLessionById(id, userId));
    }


}
