package com.example.studyE.service;

import com.example.studyE.dto.request.GeminiRequest;
import com.example.studyE.dto.response.GeminiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GeminiService {

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
            log.error("Lỗi khi gọi API Gemini: ", e);
            return new GeminiResponse("Lỗi khi gọi API Gemini.");
        }
    }
}
