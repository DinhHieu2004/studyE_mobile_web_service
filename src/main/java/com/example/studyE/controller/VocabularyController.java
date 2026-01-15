package com.example.studyE.controller;

import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @GetMapping("/lession/{lessionId}")
    public ResponseEntity<List<VocabularyResponse>> getByLession(@PathVariable Long lessionId) {
        return ResponseEntity.ok(vocabularyService.getVocabularyByLessionId(lessionId));
    }
    @GetMapping("/review")
    public List<VocabularyResponse> getVocabularyByLesson(@RequestParam("lessonId") Long lessonId) {
        return vocabularyService.getVocabularyReviewByLessonId(lessonId);
    }
    @GetMapping("/review2")
    public List<VocabularyResponse> getVocabularyByTopicV(@RequestParam("topicId") Long topicId) {
        return vocabularyService.getVocabularyReviewByTopicId(topicId);
    }
}

