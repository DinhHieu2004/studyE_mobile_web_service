package com.example.studyE.controller;

import com.example.studyE.dto.response.PartProgressDTO;
import com.example.studyE.entity.Sentence;
import com.example.studyE.entity.UserProgress;
import com.example.studyE.entity.UserProgressForPronounce;
import com.example.studyE.service.PracticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practice")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;



    @PostMapping("/save-result")
    public ResponseEntity<?> savePracticeResult(@RequestBody UserProgressForPronounce progress) {
        practiceService.savePracticeResult(progress);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/progress/{partId}")
    public ResponseEntity<PartProgressDTO> getPartProgress(
            @PathVariable int partId,
            @RequestParam String level) {
        return ResponseEntity.ok(practiceService.getPartProgress(partId, level));
    }



    @GetMapping("/incomplete-sentences/{partId}")
    public ResponseEntity<List<Sentence>> getIncomplete(@PathVariable int partId, @RequestParam String level) {
        return ResponseEntity.ok(practiceService.getIncompleteSentences( partId, level));
    }

    @PostMapping("/update-progress")
    public ResponseEntity<PartProgressDTO> updatePartProgress(@RequestBody Map<String, Object> body) {
        int part = (int) body.get("part");
        String level = (String) body.get("level");
        return ResponseEntity.ok(practiceService.getPartProgress(part, level));
    }
}
