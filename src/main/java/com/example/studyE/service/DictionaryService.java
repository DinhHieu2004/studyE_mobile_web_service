package com.example.studyE.service;

import com.example.studyE.dto.response.DictionaryResponse;
import com.example.studyE.entity.Dictionary;
import com.example.studyE.repository.DictionaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class DictionaryService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final DictionaryRepository dictionaryRepository;
    private final String dictionaryApiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private final String geminiApiKey = "AIzaSyAGsAZmerAMMx-bpEVYi22tvIWeq__GbOY";

    @Autowired
    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }
    public DictionaryResponse lookupWordWithVietnamese(String word) {

        Optional<Dictionary> cached = dictionaryRepository.findByWordIgnoreCase(word);
        if (cached.isPresent()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(cached.get().getResponseJson(), DictionaryResponse.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DictionaryResponse[] dictionaryResponses = restTemplate.getForObject(dictionaryApiUrl + word, DictionaryResponse[].class);
        if (dictionaryResponses == null || dictionaryResponses.length == 0) {
            return null;
        }

        DictionaryResponse response = dictionaryResponses[0];

        List<String> textsToTranslate = new ArrayList<>();

        // Map lưu vị trí dịch cho từng Definition
        // value là int[2] = {posDefinition, posExample} (-1 nếu không có)
        Map<DictionaryResponse.Definition, int[]> defToPositions = new HashMap<>();

        int idx = 0;
        for (DictionaryResponse.Meaning meaning : response.getMeanings()) {
            for (DictionaryResponse.Definition def : meaning.getDefinitions()) {
                int posDef = -1;
                int posExample = -1;

                if (def.getDefinition() != null) {
                    textsToTranslate.add(def.getDefinition());
                    posDef = idx++;
                }
                if (def.getExample() != null) {
                    textsToTranslate.add(def.getExample());
                    posExample = idx++;
                }

                defToPositions.put(def, new int[]{posDef, posExample});
            }
        }

        List<String> translated = translateBatchWithGemini(textsToTranslate);

        for (Map.Entry<DictionaryResponse.Definition, int[]> entry : defToPositions.entrySet()) {
            DictionaryResponse.Definition def = entry.getKey();
            int[] positions = entry.getValue();

            if (positions[0] != -1 && positions[0] < translated.size()) {
                def.setVietnameseDefinition(translated.get(positions[0]));
            }
            if (positions[1] != -1 && positions[1] < translated.size()) {
                def.setVietnameseExample(translated.get(positions[1]));
            }
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(response);
            Dictionary dictionary = Dictionary.builder()
                    .word(word.toLowerCase())
                    .responseJson(json)
                    .createdAt(new Date())
                    .build();
            dictionaryRepository.save(dictionary);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private List<String> translateBatchWithGemini(List<String> texts) {
        try {
            if (texts.isEmpty()) return List.of();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            StringBuilder prompt = new StringBuilder("Dịch sang tiếng Việt và giữ đúng thứ tự:\n");
            for (int i = 0; i < texts.size(); i++) {
                prompt.append(i + 1).append(". ").append(texts.get(i)).append("\n");
            }

            Map<String, Object> payload = Map.of(
                    "contents", List.of(Map.of(
                            "parts", List.of(Map.of("text", prompt.toString()))
                    ))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    geminiApiUrl + geminiApiKey, request, Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map body = response.getBody();
                var candidates = (List<Map>) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    var content = (Map<String, Object>) candidates.get(0).get("content");
                    var parts = (List<Map<String, Object>>) content.get("parts");
                    String fullText = (String) parts.get(0).get("text");

                    List<String> results = new ArrayList<>();
                    for (String line : fullText.split("\n")) {
                        if (line.matches("^\\d+\\.\\s+.*")) {
                            results.add(line.replaceFirst("^\\d+\\.\\s+", "").trim());
                        }
                    }
                    return results;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.nCopies(texts.size(), "");
    }

}
