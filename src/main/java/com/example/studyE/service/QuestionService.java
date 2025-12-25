package com.example.studyE.service;

import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import com.example.studyE.dto.response.OpenTriviaResponse;
import com.example.studyE.dto.response.QuizResultResponse;
import com.example.studyE.entity.Option;
import com.example.studyE.entity.Question;
import com.example.studyE.entity.QuizResult;
import com.example.studyE.entity.User;
import com.example.studyE.exception.AppException;
import com.example.studyE.exception.ErrorCode;
import com.example.studyE.mapper.QuestionMapper;
import com.example.studyE.mapper.QuizResultMapper;
import com.example.studyE.repository.*;
import com.example.studyE.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuestionService {

    QuestionRepository questionRepository;
    UserQuestionHistoryRepository userQuestionHistoryRepository;
    OptionRepository optionRepository;
    QuestionMapper questionMapper;
    RestTemplate restTemplate;
    UserRepository userRepository;
    AnswerDetailRepository answerDetailRepository;
    QuizResultRepository quizResultRepository;
    QuizResultMapper quizResultMapper;



    public List<OpenTriviaQuestionResponse> fetchAndReturnQuestions( int amount, String difficulty, String category) {

        Long userId = JwtUtil.getUserIdFromToken();

        try {
        Set<String> answeredQuestionContents = new HashSet<>(answerDetailRepository.findAnsweredQuestionTextsByUserId(userId));


        String url = String.format(
                "https://opentdb.com/api.php?amount=%d&difficulty=%s&type=multiple&category=%s",
                amount * 3, difficulty, category);

        ResponseEntity<OpenTriviaResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OpenTriviaResponse>() {
                }
        );

        if (response.getBody() == null || response.getBody().getResults() == null) {
            return new ArrayList<>();
        }

        List<OpenTriviaQuestionResponse> finalQuestions = new ArrayList<>();

        for (OpenTriviaQuestionResponse dto : response.getBody().getResults()) {
            String questionContent = HtmlUtils.htmlUnescape(dto.getQuestion());

            if (answeredQuestionContents.contains(questionContent)) {
                continue;
            }

            Question question = questionRepository.findByContent(questionContent).orElse(null);
            if (question == null) {
                question = questionMapper.toEntity(dto);

                List<Option> options = new ArrayList<>();
                options.add(Option.builder()
                        .content(HtmlUtils.htmlUnescape(dto.getCorrectAnswer()))
                        .question(question)
                        .isCorrect(true)
                        .build());

                for (String incorrect : dto.getIncorrectAnswers()) {
                    options.add(Option.builder()
                            .content(HtmlUtils.htmlUnescape(incorrect))
                            .question(question)
                            .isCorrect(false)
                            .build());
                }

                Collections.shuffle(options);
                question.setOptions(options);
                question = questionRepository.save(question);
            }

            finalQuestions.add(dto);

            if (finalQuestions.size() == amount) {
                break;
            }
        }


        return finalQuestions;

        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new RuntimeException("Hệ thống câu hỏi đang bận, vui lòng đợi 5 giây.");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hệ thống khi lấy câu hỏi.");
        }
    }

    public List<QuizResultResponse> getQuizHistoryUser(LocalDate startDate, LocalDate endDate) {
        Long userId = JwtUtil.getUserIdFromToken();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<QuizResult> list =quizResultRepository.findAllByUserIdAndTimestampBetween(userId, start, end);

        return list.stream().map(quizResultMapper::toQuizResultResponse)
                .collect(Collectors.toList());

    }

}