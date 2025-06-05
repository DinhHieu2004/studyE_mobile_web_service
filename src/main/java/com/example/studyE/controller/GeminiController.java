package com.example.studyE.controller;

import com.example.studyE.dto.request.GeminiRequest;
import com.example.studyE.dto.response.GeminiResponse;
import com.example.studyE.service.GeminiService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
