package com.example.studyE.controller;


import com.example.studyE.dto.response.SentencePartResponse;
import com.example.studyE.dto.response.SentenceResponse;
import com.example.studyE.service.SentenceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sentence")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SentenceController {

    SentenceService sentenceService;

    @GetMapping("/part")
    ResponseEntity<SentencePartResponse> getListPart(@RequestParam String level){
        return ResponseEntity.ok(sentenceService.getPartList(level));
    }

    @GetMapping
    ResponseEntity<List<SentenceResponse>> getListSentenceByPartAndLevel(@RequestParam String level, @RequestParam Integer part){
        return ResponseEntity.ok(sentenceService.getSentenceByPartAndLevel(level, part));
    }

}
