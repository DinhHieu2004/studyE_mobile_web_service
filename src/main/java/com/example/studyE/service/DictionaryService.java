package com.example.studyE.service;
import com.google.genai.Client;
import com.example.studyE.dto.response.DictionaryResponse;
import com.example.studyE.entity.Dictionary;
import com.example.studyE.repository.DictionaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final Client geminiClient;
    private static final String GEMINI_API_KEY =
            "AIzaSyC4-W2RQYPBFrge50TdEhVP_4T_ipXeusc";
    private final String dictionaryApiUrl =
    "https://api.dictionaryapi.dev/api/v2/entries/en/";

    @Autowired
    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.geminiClient = Client.builder()
                .apiKey(GEMINI_API_KEY)
                .build();
    }
    public DictionaryResponse lookupWordWithVietnamese(String word) {

        // ===== CACHE =====
        Optional<Dictionary> cached =
                dictionaryRepository.findByWordIgnoreCase(word);
        if (cached.isPresent()) {
            try {
                return new ObjectMapper()
                        .readValue(cached.get().getResponseJson(),
                                DictionaryResponse.class);
            } catch (Exception ignored) {}
        }

        // ===== EN DICTIONARY =====
        DictionaryResponse[] responses =
                restTemplate.getForObject(
                        dictionaryApiUrl + word,
                        DictionaryResponse[].class
                );

        if (responses == null || responses.length == 0) return null;
        DictionaryResponse response = responses[0];

        // ===== COLLECT TEXT =====
        List<String> texts = new ArrayList<>();
        Map<DictionaryResponse.Definition, int[]> map = new HashMap<>();
        int idx = 0;

        for (var meaning : response.getMeanings()) {
            for (var def : meaning.getDefinitions()) {
                int defPos = -1, exPos = -1;

                if (def.getDefinition() != null) {
                    texts.add(def.getDefinition());
                    defPos = idx++;
                }
                if (def.getExample() != null) {
                    texts.add(def.getExample());
                    exPos = idx++;
                }
                map.put(def, new int[]{defPos, exPos});
            }
        }

        // ===== GEMINI TRANSLATE =====
        List<String> translated = translateBatchWithGemini(texts);

        // ===== MAP BACK =====
        for (var e : map.entrySet()) {
            var def = e.getKey();
            var pos = e.getValue();

            if (pos[0] >= 0) def.setVietnameseDefinition(translated.get(pos[0]));
            if (pos[1] >= 0) def.setVietnameseExample(translated.get(pos[1]));
        }

        // ===== SAVE CACHE =====
        try {
            String json = new ObjectMapper().writeValueAsString(response);
            dictionaryRepository.save(
                    Dictionary.builder()
                            .word(word.toLowerCase())
                            .responseJson(json)
                            .createdAt(new Date())
                            .build()
            );
        } catch (Exception ignored) {}

        return response;
    }

    // ================= GEMINI =================

    private List<String> translateBatchWithGemini(List<String> texts) {
        if (texts.isEmpty()) return List.of();

        StringBuilder prompt = new StringBuilder(
                "Dịch sang tiếng Việt, giữ nguyên thứ tự. " +
                        "Chỉ trả về nội dung đã dịch:\n"
        );

        for (int i = 0; i < texts.size(); i++) {
            prompt.append(i + 1).append(". ").append(texts.get(i)).append("\n");
        }

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        "gemini-2.5-flash",
                        prompt.toString(),
                        null
                );

        String text = response.text();
        List<String> result = new ArrayList<>();

        for (String line : text.split("\n")) {
            if (line.matches("^\\d+\\.\\s+.*")) {
                result.add(line.replaceFirst("^\\d+\\.\\s+", "").trim());
            }
        }

        while (result.size() < texts.size()) result.add("");
        return result;
    }
}
