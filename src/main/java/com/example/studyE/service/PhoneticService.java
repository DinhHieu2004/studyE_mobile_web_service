package com.example.studyE.service;

import com.example.studyE.configuaration.AppConfig;
import com.example.studyE.dto.request.PhoneticRequest;
import com.example.studyE.dto.response.PhoneticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PhoneticService {

    private final AppConfig appConfig;

    private final String url = "https://dfda31beecbb.ngrok-free.app//api/phonemize";

    public PhoneticResponse getPhonetic(PhoneticRequest request) {

        System.out.println("REQUEST TEXT = " + request.getText());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PhoneticRequest> entity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<PhoneticResponse> response =
                    appConfig.restTemplate().postForEntity(
                            url,
                            entity,
                            PhoneticResponse.class
                    );
            return response.getBody();
        } catch (HttpServerErrorException e) {
            System.out.println("FLASK RESPONSE:");
            System.out.println(e.getResponseBodyAsString());
            throw e;
        }
    }
}

