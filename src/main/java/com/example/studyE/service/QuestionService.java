package com.example.studyE.service;

import com.example.studyE.Entity.Option;
import com.example.studyE.Entity.Question;
import com.example.studyE.Entity.User;
import com.example.studyE.Entity.UserQuestionHistory;
import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import com.example.studyE.dto.response.OpenTriviaResponse;
import com.example.studyE.exception.AppException;
import com.example.studyE.exception.ErrorCode;
import com.example.studyE.mapper.QuestionMapper;
import com.example.studyE.repository.OptionRepository;
import com.example.studyE.repository.QuestionRepository;
import com.example.studyE.repository.UserQuestionHistoryRepository;
import com.example.studyE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<OpenTriviaQuestionResponse> fetchAndReturnQuestions(String username, int amount, String difficulty, String category) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String url = String.format(
                "https://opentdb.com/api.php?amount=%d&difficulty=%s&type=multiple&category=%s",
                amount * 3, difficulty, category);

        ResponseEntity<OpenTriviaResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<OpenTriviaResponse>() {}
        );

        if (response.getBody() == null || response.getBody().getResults() == null) {
            return new ArrayList<>();
        }

        List<OpenTriviaQuestionResponse> finalQuestions = new ArrayList<>();

        for (OpenTriviaQuestionResponse dto : response.getBody().getResults()) {
            String questionContent = HtmlUtils.htmlUnescape(dto.getQuestion());

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

            if (!userQuestionHistoryRepository.existsByUser_UsernameAndQuestion(username, question)) {

                UserQuestionHistory userQuestionHistory = UserQuestionHistory.builder()
                        .user(user)
                        .question(question)
                        .build();
                userQuestionHistoryRepository.save(userQuestionHistory);
                finalQuestions.add(dto);
            }

            if (finalQuestions.size() == amount) break;
        }

        return finalQuestions;
    }

}