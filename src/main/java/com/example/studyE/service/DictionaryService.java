package com.example.studyE.service;

import com.example.studyE.dto.response.DictionaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DictionaryService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String dictionaryApiUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/";
    private final String myMemoryTranslateUrl = "https://api.mymemory.translated.net/get";

    public DictionaryResponse lookupWordWithVietnamese(String word) {
        // 1. Gọi API từ điển
        DictionaryResponse[] dictionaryResponses = restTemplate.getForObject(dictionaryApiUrl + word, DictionaryResponse[].class);

        if (dictionaryResponses == null || dictionaryResponses.length == 0) {
            return null;
        }

        DictionaryResponse response = dictionaryResponses[0];

        // 2. Dịch từng definition & example sang tiếng Việt
        for (DictionaryResponse.Meaning meaning : response.getMeanings()) {
            for (DictionaryResponse.Definition def : meaning.getDefinitions()) {
                if (def.getDefinition() != null && !def.getDefinition().isEmpty()) {
                    def.setVietnameseDefinition(translate(def.getDefinition()));
                }
                if (def.getExample() != null && !def.getExample().isEmpty()) {
                    def.setVietnameseExample(translate(def.getExample()));
                }
            }
        }

        return response;
    }

    private String translate(String text) {
        try {
            // Encode URL cho text
            String encodedText = java.net.URLEncoder.encode(text, "UTF-8");
            String url = myMemoryTranslateUrl + "?q=" + encodedText + "&langpair=en|vi";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                if (body != null && body.containsKey("responseData")) {
                    Map<String, Object> responseData = (Map<String, Object>) body.get("responseData");
                    return (String) responseData.get("translatedText");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
