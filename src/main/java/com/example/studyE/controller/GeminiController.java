package com.example.studyE.controller;

import com.example.studyE.dto.request.GeminiRequest;
import com.example.studyE.dto.request.GeminiSentenceRequest;
import com.example.studyE.dto.response.GeminiResponse;
import com.example.studyE.service.GeminiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gemini")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GeminiController {

    GeminiService geminiService;

    @PostMapping
    ResponseEntity<GeminiResponse> getAnswerGemini(@RequestBody GeminiRequest request){
        return ResponseEntity.ok(geminiService.getAnswerToGemini(request));
    }
    @PostMapping("/sentence")
    ResponseEntity<List<String>> getAnswerSentence(@RequestBody GeminiSentenceRequest request){
        return ResponseEntity.ok(geminiService.generateSentencesByLevel(request));
    }
    @GetMapping("/update-part")
    ResponseEntity<String> update(@RequestParam String level){
        geminiService.assignPartsToExistingSentences(level);
        return ResponseEntity.ok("Success");
    }
}
