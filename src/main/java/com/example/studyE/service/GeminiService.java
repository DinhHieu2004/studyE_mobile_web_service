package com.example.studyE.service;

import com.example.studyE.dto.request.GeminiRequest;
import com.example.studyE.dto.request.GeminiSentenceRequest;
import com.example.studyE.dto.response.GeminiResponse;
import com.example.studyE.entity.Sentence;
import com.example.studyE.repository.SentenceRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GeminiService {

    SentenceRepository sentenceRepository;

    String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/";
    String API_KEY = "AIzaSyBaGr7-s975QtwVtAE8g6buZri0Gg3yfHA";
    String MODEL_PATH = "models/gemini-1.5-flash:generateContent";

    RestTemplate restTemplate;

    public GeminiResponse getAnswerToGemini(GeminiRequest request) {
        String prompt = "Câu hỏi: " + request.getQuestion()
                + "\nĐáp án đã chọn: " + request.getAnswer()
                + "\nGiải thích chi tiết giúp tôi hiểu bài. giải thích bằng tiếng việt";

        Map<String, Object> message = Map.of(
                "parts", List.of(Map.of("text", prompt))
        );
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(message)
        );

        String url = BASE_URL + MODEL_PATH + "?key=" + API_KEY;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map result = response.getBody();
            if (result != null && result.containsKey("candidates")) {
                List candidates = (List) result.get("candidates");
                if (!candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    if (!parts.isEmpty()) {
                        Map part = (Map) parts.get(0);


                        String answer =  (String) part.get("text");
                        return new GeminiResponse(answer);
                    }
                }
            }

            return new GeminiResponse("Không có câu trả lời từ Gemini.");

        } catch (Exception e) {
            GeminiService.log.error("Lỗi khi gọi API Gemini: ", e);
            return new GeminiResponse("Lỗi khi gọi API Gemini.");
        }
    }

    public List<String> generateSentencesByLevel(GeminiSentenceRequest request) {
        String prompt = String.format(
                "Hãy tạo %d câu tiếng Anh đơn giản ở trình độ %s. Mỗi câu nằm trên một dòng riêng.",
                request.getCount(), request.getLevel()
        );

        Map<String, Object> message = Map.of(
                "parts", List.of(Map.of("text", prompt))
        );
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(message)
        );

        String url = BASE_URL + MODEL_PATH + "?key=" + API_KEY;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map result = response.getBody();
            if (result != null && result.containsKey("candidates")) {
                List candidates = (List) result.get("candidates");
                if (!candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    if (!parts.isEmpty()) {
                        Map part = (Map) parts.get(0);
                        String answer = (String) part.get("text");

                        List<String> sentences = Arrays.stream(answer.split("\\r?\\n"))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();

                        List<Sentence> newSentences = new ArrayList<>();

                        for (String s : sentences) {
                            if (!sentenceRepository.existsByContentAndLevel(s, request.getLevel())) {
                                Sentence sentence = new Sentence();
                                sentence.setContent(s);
                                sentence.setLevel(request.getLevel());
                                newSentences.add(sentence);
                            }
                        }

                        sentenceRepository.saveAll(newSentences);
                        assignPartsToExistingSentences(request.getLevel());
                        return sentences;
                    }
                }
            }

            throw new RuntimeException("Không có câu trả lời hợp lệ từ Gemini");

        } catch (Exception e) {
            GeminiService.log.error("Lỗi khi gọi API Gemini (generateSentencesByLevel): ", e);
            throw new RuntimeException("Không thể lấy dữ liệu từ Gemini");
        }
    }

    @Transactional
    public void assignPartsToExistingSentences(String level) {
        try {
            // Lấy tất cả câu cho trình độ được chỉ định
            List<Sentence> sentences = sentenceRepository.findAllByLevel(level);

            if (sentences.isEmpty()) {
                throw new RuntimeException("Không có câu nào cho trình độ " + level);
            }

            int totalSentences = sentences.size();
            System.out.println("level " + level + " tổng sentence: " + totalSentences);

            int parts = 5;
            int baseSentencesPerPart = totalSentences / parts; // Số câu cơ bản cho mỗi phần
            int extraSentences = totalSentences % parts;       // Số câu thừa cần phân bổ

            int index = 0;
            for (int partNumber = 1; partNumber <= parts; partNumber++) {
                int partSize = baseSentencesPerPart + (extraSentences > 0 ? 1 : 0);
                if (extraSentences > 0) {
                    extraSentences--;
                }

                for (int i = 0; i < partSize && index < totalSentences; i++) {
                    if (index < sentences.size()) {
                        Sentence sentence = sentences.get(index);
                        log.info("Cập nhật sentence ID {}: part_number trước = {}, sau = {}",
                                sentence.getId(), sentence.getPartNumber(), partNumber);
                        sentence.setPartNumber(partNumber);
                        index++;
                    }
                }
            }

            // Lưu tất cả câu đã cập nhật
            sentenceRepository.saveAll(sentences);
            log.info("Phân bổ part_number hoàn tất cho {} câu với trình độ {}", totalSentences, level);

        } catch (Exception e) {
            log.error("Lỗi khi gán part cho câu hiện có ở trình độ " + level + ": ", e);
            throw new RuntimeException("Không thể gán part cho câu ở trình độ " + level);
        }
    }


}
