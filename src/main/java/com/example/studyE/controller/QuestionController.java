package com.example.studyE.controller;

import com.example.studyE.dto.request.GetQuestionRequest;
import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import com.example.studyE.service.QuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;

    @GetMapping
    public ResponseEntity<List<OpenTriviaQuestionResponse>> getQuestions(@RequestBody GetQuestionRequest request) {
        log.info("Fetching questions with: amount={}, difficulty={}, category={}",
                request.getAmount(), request.getDifficulty(), request.getCategory());

        List<OpenTriviaQuestionResponse> questions = questionService.fetchAndReturnQuestions(
                request.getAmount(), request.getDifficulty(), request.getCategory()
        );

        return ResponseEntity.ok(questions);
    }
}
