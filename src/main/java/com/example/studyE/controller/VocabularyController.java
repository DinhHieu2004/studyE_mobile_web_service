package com.example.studyE.controller;

import com.example.studyE.dto.request.UnlockConfirmRequest;
import com.example.studyE.dto.response.UnlockQuestionDTO;
import com.example.studyE.dto.response.UnlockableVocabularyResponse;
import com.example.studyE.dto.response.VocabularyCardPreviewDTO;
import com.example.studyE.dto.response.VocabularyResponse;
import com.example.studyE.entity.VocabularyCard;
import com.example.studyE.service.UnlockQuestionService;
import com.example.studyE.service.VocabularyCardImportPreviewService;
import com.example.studyE.service.VocabularyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vocabularies")
@RequiredArgsConstructor
public class VocabularyController {

    private final VocabularyService vocabularyService;
    private final VocabularyCardImportPreviewService previewService;
    private final UnlockQuestionService unlockQuestionService;

    @GetMapping("/lession/{lessionId}")
    public ResponseEntity<List<VocabularyResponse>> getByLession(@PathVariable Long lessionId) {
        return ResponseEntity.ok(vocabularyService.getVocabularyByLessionId(lessionId));
    }
    @GetMapping("/review")
    public List<VocabularyResponse> getVocabularyByLesson(@RequestParam("lessonId") Long lessonId) {
        return vocabularyService.getVocabularyReviewByLessonId(lessonId);
    }
    @GetMapping("/review2")
    public List<VocabularyResponse> getVocabularyByTopicV(@RequestParam("topicId") Long topicId) {
        return vocabularyService.getVocabularyReviewByTopicId(topicId, getCurrentUserId());
    }
    @GetMapping()
    public List<VocabularyCardPreviewDTO> getAll() {
        return vocabularyService.getVocabularyAlls();
    }
    @GetMapping("/unlockable")
    public List<UnlockableVocabularyResponse> getVocabularyUnlock(@RequestParam("topicId") Long topicId) {
        return vocabularyService.getVocabularyUnlockable(topicId, getCurrentUserId());
    }
    @PostMapping("/import/preview")
    public ResponseEntity<List<VocabularyCardPreviewDTO>> preview(
            @RequestParam("file") MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File is empty"
            );
        }

        return ResponseEntity.ok(previewService.previewExcel(file));
    }
    @PostMapping("/import")
    public ResponseEntity<Void> importFromPreview(
            @RequestBody List<VocabularyCardPreviewDTO> items
    ) {
        previewService.importFromPreview(items);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/unlock-question")
    public UnlockQuestionDTO getUnlockQuestion(
            @RequestParam Long cardId
    ) {
        return unlockQuestionService.getUnlockQuestion(getCurrentUserId(), cardId);
    }
    @PostMapping("/unlock-confirm")
    public UnlockableVocabularyResponse confirmUnlock(
            @RequestBody UnlockConfirmRequest req
    ) {
        System.out.println(req.getQuestionId());
        System.out.println(req.getAnswerIndex());

        Long userId = getCurrentUserId();
        return unlockQuestionService.confirmUnlock(
                userId,
                req.getQuestionId(),
                req.getAnswerIndex()
        );
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Map<?, ?> principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return Long.valueOf(principal.get("userId").toString());
    }
}

