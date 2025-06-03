package com.example.studyE.controller;


import com.example.studyE.Entity.QuizResult;
import com.example.studyE.dto.request.QuizResultRequest;
import com.example.studyE.dto.response.ApiResponse;
import com.example.studyE.service.QuizResultService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz-results")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizResultController {
    QuizResultService quizResultService;


    @PostMapping
    ApiResponse<Void> save(@RequestBody QuizResultRequest  quizResult) {

        log.info("request save: " +quizResult.toString());


        quizResultService.saveQuizResult(quizResult);
        return ApiResponse.<Void>builder().result(null).build();

    }
}
