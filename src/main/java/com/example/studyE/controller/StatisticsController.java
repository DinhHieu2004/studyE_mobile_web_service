package com.example.studyE.controller;


import com.example.studyE.dto.response.ApiResponse;
import com.example.studyE.dto.response.StatisticsResponse;
import com.example.studyE.service.StatisticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsController {
    StatisticsService statisticsService;

    @GetMapping
    ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getUserStatistics());
    }
}
