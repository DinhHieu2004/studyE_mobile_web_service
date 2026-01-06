package com.example.studyE.service;

import com.example.studyE.dto.response.OpenTriviaQuestionResponse;
import com.example.studyE.dto.response.OpenTriviaResponse;
import com.example.studyE.dto.response.PageResponse;
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
import com.opencsv.CSVReader;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.io.InputStreamReader;
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



    public void delete(String id){
        Long idl = Long.parseLong(id);

        Question q = questionRepository.findById(idl)
                .orElseThrow(RuntimeException::new);

        q.setDeleted(true);
        questionRepository.save(q);
    }

    public PageResponse<OpenTriviaQuestionResponse> getAllQuestion(int page, int size){


        Pageable pageable = PageRequest.of(page -1, size);

        var pageData = questionRepository.findAll(pageable);

        var questionList = pageData.getContent().stream().map(
                q -> {
                    var questionR  = questionMapper.toResponse(q);
                    return questionR;
                }
        ).toList();

        return PageResponse.<OpenTriviaQuestionResponse>builder()
                .totalPage(pageData.getTotalPages())
                .pageNo(page)
                .totalItems(pageData.getTotalElements())
                .items(questionList)
                .isLast(pageData.isLast())
                .pageSize(size)
                .build();

    }


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


    public void importFromFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new RuntimeException("File rỗng");

        if (filename.endsWith(".csv")) importFromCsv(file);
        else if (filename.endsWith(".xlsx")) importFromExcel(file);
        else throw new RuntimeException("Chỉ hỗ trợ CSV hoặc Excel");
    }

    private void importFromCsv(MultipartFile file) throws Exception {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();
            if (!rows.isEmpty()) rows.remove(0);

            for (String[] r : rows) {
                saveQuestion(
                        r[0], r[1], r[2], r[3], r[4], r[5],
                        r.length > 6 ? r[6] : null,
                        r.length > 7 ? r[7] : null,
                        r.length > 8 ? r[8] : null
                );
            }
        }
    }


    private void importFromExcel(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                saveQuestion(
                        getCell(r,0), getCell(r,1), getCell(r,2),
                        getCell(r,3), getCell(r,4), getCell(r,5),
                        getCell(r,6), getCell(r,7), getCell(r,8)
                );
            }
        }
    }


    private String getCell(Row r, int i) {
        if (r == null || r.getCell(i) == null) return null;
        return r.getCell(i).toString().trim();
    }

    private void saveQuestion(String q, String a, String b, String c, String d,
                              String correct, String category, String difficulty, String type) {

        if (q == null || a == null || b == null || c == null || d == null || correct == null) return;

        q = HtmlUtils.htmlUnescape(q).trim();
        correct = correct.trim().toUpperCase();

        if (questionRepository.existsByContent(q)) return;

        Question question = Question.builder()
                .content(q)
                .category(category)
                .difficulty(difficulty)
                .type(type)
                .build();

        List<Option> options = new ArrayList<>();
        Option correctOption = null;

        Map<String,String> map = Map.of(
                "A", a, "B", b, "C", c, "D", d
        );

        for (Map.Entry<String,String> e : map.entrySet()) {
            boolean isCorrect = e.getKey().equals(correct);
            Option opt = buildOption(question, e.getValue(), isCorrect);
            if (isCorrect) correctOption = opt;
            options.add(opt);
        }

        if (correctOption != null) {
            question.setCorrectAnswer(correctOption.getContent());
        }

        Collections.shuffle(options);
        question.setOptions(options);
        questionRepository.save(question);
    }


    private Option buildOption(Question question, String content, boolean isCorrect) {
        return Option.builder()
                .content(content)
                .isCorrect(isCorrect)
                .question(question)
                .build();
    }
}
