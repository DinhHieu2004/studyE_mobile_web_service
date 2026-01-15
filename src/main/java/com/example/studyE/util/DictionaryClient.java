package com.example.studyE.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.cache.annotation.Cacheable;

@Component
public class DictionaryClient {
    private final RestClient rc;

    public DictionaryClient(@Value("${app.dictionary.base-url}") String baseUrl) {
        this.rc = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Cacheable(cacheNames = "dictCache", key = "#word.toLowerCase()")
    public DictInfo lookup(String word) {
        try {
            JsonNode node = rc.get()
                    .uri("/api/v2/entries/en/{w}", word)
                    .retrieve()
                    .body(JsonNode.class);

            if (node == null || !node.isArray() || node.size() == 0) return DictInfo.empty(word);
            JsonNode first = node.get(0);

            String phonetic = first.path("phonetic").asText("");
            String audioUrl = "";

            for (JsonNode p : first.path("phonetics")) {
                if (phonetic.isBlank()) phonetic = p.path("text").asText("");
                String a = p.path("audio").asText("");
                if (!a.isBlank()) { audioUrl = a; break; }
            }

            String defEn = "";
            JsonNode meanings = first.path("meanings");
            if (meanings.isArray() && meanings.size() > 0) {
                JsonNode defs = meanings.get(0).path("definitions");
                if (defs.isArray() && defs.size() > 0) {
                    defEn = defs.get(0).path("definition").asText("");
                }
            }

            return new DictInfo(word, phonetic, audioUrl, defEn);

        } catch (HttpClientErrorException.NotFound e) {
            return DictInfo.empty(word);
        } catch (Exception e) {
            return DictInfo.empty(word);
        }
    }

    public record DictInfo(String word, String phonetic, String audioUrl, String definitionEn) {
        static DictInfo empty(String w) { return new DictInfo(w, "", "", ""); }
    }
}

