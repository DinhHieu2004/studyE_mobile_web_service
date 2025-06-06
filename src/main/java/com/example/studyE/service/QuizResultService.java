package com.example.studyE.service;


import com.example.studyE.dto.request.QuizResultRequest;
import com.example.studyE.entity.AnswerDetail;
import com.example.studyE.entity.QuizResult;
import com.example.studyE.mapper.QuizResultMapper;
import com.example.studyE.repository.QuizResultRepository;
import com.example.studyE.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuizResultService {

    QuizResultRepository quizResultRepository;

    QuizResultMapper mapper;

    public void saveQuizResult(QuizResultRequest dto) {

        Long userId = JwtUtil.getUserIdFromToken();

        QuizResult quizResult = mapper.toEntity(dto);

        List<AnswerDetail> answerEntities = dto.getAnswers().stream()
                .map(answerDTO -> {
                    AnswerDetail entity = mapper.toEntity(answerDTO);
                    entity.setQuizResult(quizResult);
                    return entity;
                })
                .collect(Collectors.toList());

        quizResult.setAnswers(answerEntities);
        quizResult.setUserId(userId);

        quizResultRepository.save(quizResult);
    }
}
