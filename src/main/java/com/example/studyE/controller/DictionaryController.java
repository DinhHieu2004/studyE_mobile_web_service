package com.example.studyE.controller;

import com.example.studyE.dto.response.DictionaryResponse;
import com.example.studyE.service.DictionaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dictionary")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DictionaryController {

    @Autowired
    private DictionaryService dictionaryService;

    @GetMapping("/lookup")
    public ResponseEntity<DictionaryResponse> lookupWord(@RequestParam String word) {
        DictionaryResponse response = dictionaryService.lookupWordWithVietnamese(word);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
