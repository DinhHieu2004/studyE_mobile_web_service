package com.example.studyE.controller;

import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

