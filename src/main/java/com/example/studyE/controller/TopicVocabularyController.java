package com.example.studyE.controller;

import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.service.HomeService;
import com.example.studyE.service.TopicVocabularyService;
import com.example.studyE.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/topicV")
@RequiredArgsConstructor
public class TopicVocabularyController {
    private final TopicVocabularyService topicVocabularyService;

    @GetMapping()
    public List<TopicHomeResponse> getAlls() {
        return topicVocabularyService.getListTopicVocabulary();
    }
    @GetMapping("/watched")
    public List<TopicHomeResponse> getWatched() {
        return topicVocabularyService.getTopicWatched(getCurrentUserId());
    }

    @PostMapping("/watched")
    public ResponseEntity<Void> markWatched(@RequestParam Long topicId) {
        topicVocabularyService.markAsWatched(topicId, getCurrentUserId());
        return ResponseEntity.ok().build();
    }
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?, ?> principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return Long.valueOf(principal.get("userId").toString());
    }

}
