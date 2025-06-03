package com.example.studyE.service;

import com.example.studyE.dto.response.CategoryStatsResponse;
import com.example.studyE.dto.response.ProgressStatsResponse;
import com.example.studyE.dto.response.StatisticsResponse;
import com.example.studyE.repository.AnswerDetailRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class StatisticsService {
    AnswerDetailRepository answerDetailRepository;

    public StatisticsResponse getUserStatistics() {
        Long userId = 1L;
        List<Object[]> results = answerDetailRepository.getUserStats(userId);

        Object[] result = results.get(0);
        long total = ((Number) result[0]).longValue();
        long correct = result[1] == null ? 0 : ((Number) result[1]).longValue();
        double accuracy = total == 0 ? 0 : (correct * 100.0 / total);

        List<CategoryStatsResponse> categoryStats = answerDetailRepository.getStatsByCategory(userId)
                .stream()
                .map(row -> {
                    String categoryName = (String) row[0];
                    long t = ((Number) row[1]).longValue();
                    long c = row[2] == null ? 0 : ((Number) row[2]).longValue();
                    double acc = t == 0 ? 0 : (c * 100.0 / t);
                    return new CategoryStatsResponse(categoryName, t, c, acc);
                }).toList();

        List<ProgressStatsResponse> progressStats = answerDetailRepository.getProgressByDate(userId)
                .stream()
                .map(row -> {
                    LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
                    long t = ((Number) row[1]).longValue();
                    long c = row[2] == null ? 0 : ((Number) row[2]).longValue();
                    double acc = t == 0 ? 0 : (c * 100.0 / t);
                    return new ProgressStatsResponse(date, t, c, acc);
                }).toList();


        return new StatisticsResponse(total, correct, accuracy, categoryStats, progressStats);
    }



}
