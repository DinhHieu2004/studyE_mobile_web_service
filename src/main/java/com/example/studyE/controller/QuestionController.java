package com.example.studyE.controller;

import com.example.studyE.dto.request.GetQuestionRequest;
import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import com.example.studyE.dto.response.QuizResultResponse;
import com.example.studyE.service.QuestionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionController {
    QuestionService questionService;

    @PostMapping
    public ResponseEntity<List<OpenTriviaQuestionResponse>> getQuestions(@RequestBody GetQuestionRequest request) {
        log.info("Fetching questions with: amount={}, difficulty={}, category={}",
                request.getAmount(), request.getDifficulty(), request.getCategory());

        List<OpenTriviaQuestionResponse> questions = questionService.fetchAndReturnQuestions(
                request.getAmount(), request.getDifficulty(), request.getCategory()
        );
        log.info("Fetched {} questions", questions.size());

        return ResponseEntity.ok(questions);
    }

    @GetMapping("/history")
    public ResponseEntity<List<QuizResultResponse>> getQuizResults(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
                                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end) {
        log.info("Fetching quiz results");
        long userId = 1L;
        return ResponseEntity.ok(questionService.getQuizHistoryUser(start, end));
    }
}
