package com.example.studyE.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.cache.annotation.Cacheable;

@Component
public class MyMemoryTranslateClient {
    private final RestClient rc;
    private final String email;

    public MyMemoryTranslateClient(
            @Value("${app.mymemory.base-url}") String baseUrl,
            @Value("${app.mymemory.contact-email:}") String email
    ) {
        this.rc = RestClient.builder().baseUrl(baseUrl).build();
        this.email = email == null ? "" : email.trim();
    }

    @Cacheable(cacheNames = "translateCache", key = "#text")
    public String enToVi(String text) {
        if (text == null || text.isBlank()) return "";

        String cleaned = text.replaceAll("\\s+", " ").trim();

        if (cleaned.length() > 400) {
            cleaned = cleaned.substring(0, 400);
        }

        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/get")
                .queryParam("q", cleaned)
                .queryParam("langpair", "en|vi");
        if (!email.isBlank()) b.queryParam("de", email);

        JsonNode node = rc.get()
                .uri(b.build().encode().toUri())
                .retrieve()
                .body(JsonNode.class);


        return node == null ? "" : node.path("responseData").path("translatedText").asText("");
    }
}

