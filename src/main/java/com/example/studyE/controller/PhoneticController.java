package com.example.studyE.controller;

import com.example.studyE.dto.request.PhoneticRequest;
import com.example.studyE.dto.response.ApiResponse;
import com.example.studyE.dto.response.PhoneticResponse;
import com.example.studyE.service.PhoneticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/phonetic")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PhoneticController {

    PhoneticService phoneticService;
    @PostMapping
    public ResponseEntity<PhoneticResponse> getPhonetic(@RequestBody PhoneticRequest request){
        return ResponseEntity.ok(phoneticService.getPhonetic(request));
    }
}
