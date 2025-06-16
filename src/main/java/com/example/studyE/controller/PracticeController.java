package com.example.studyE.controller;

import com.example.studyE.dto.response.PartProgressDTO;
import com.example.studyE.entity.Sentence;
import com.example.studyE.entity.UserProgress;
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

    // Giả sử userId lấy từ token hoặc truyền vào (demo hardcoded)
    private Long mockUserId = 1L;

    @PostMapping("/save-result")
    public ResponseEntity<?> savePracticeResult(@RequestBody UserProgress progress) {
        practiceService.savePracticeResult(mockUserId, progress);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/progress/{partId}")
    public ResponseEntity<PartProgressDTO> getPartProgress(@PathVariable int partId) {
        return ResponseEntity.ok(practiceService.getPartProgress(mockUserId, partId));
    }

    @GetMapping("/incomplete-sentences/{partId}")
    public ResponseEntity<List<Sentence>> getIncomplete(@PathVariable int partId) {
        return ResponseEntity.ok(practiceService.getIncompleteSentences(mockUserId, partId));
    }

    @PostMapping("/update-progress")
    public ResponseEntity<PartProgressDTO> updatePartProgress(@RequestBody Map<String, Object> body) {
        int part = (int) body.get("part");
        return ResponseEntity.ok(practiceService.getPartProgress(mockUserId, part));
    }
}
