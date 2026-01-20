package com.example.studyE.service;

import com.example.studyE.dto.request.VocabularyUpdateRequest;
import com.example.studyE.dto.response.VocabularyCardPreviewDTO;
import com.example.studyE.entity.UnlockQuestion;
import com.example.studyE.entity.UnlockQuestionOption;
import com.example.studyE.entity.VocabularyCard;
import com.example.studyE.repository.TopicVocabularyRepository;
import com.example.studyE.repository.UnlockQuestionOptionRepository;
import com.example.studyE.repository.UnlockQuestionRepository;
import com.example.studyE.repository.VocabularyCardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VocabularyCardImportPreviewService {
    private final VocabularyCardRepository vocabularyRepo;
    private final TopicVocabularyRepository topicRepo;
    private final UnlockQuestionOptionRepository optionRepo;
    private final UnlockQuestionRepository unlockQuestionRepository;
    public List<VocabularyCardPreviewDTO> previewExcel(MultipartFile file) {

        List<VocabularyCardPreviewDTO> result = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                VocabularyCardPreviewDTO dto = new VocabularyCardPreviewDTO();
                dto.setRow(i + 1);
                dto.setWord(getString(row, 0));
                dto.setTopicId(getLong(row, 1));
                dto.setMeaning(getString(row, 2));
                dto.setPhonetic(getString(row, 3));
                dto.setExample(getString(row, 4));
                dto.setExampleMeaning(getString(row, 5));
                dto.setImageUrl(getString(row, 6));
                dto.setAudioUrl(getString(row, 7));

                result.add(dto);
            }

        } catch (IOException e) {
            throw new RuntimeException("Preview excel failed", e);
        }

        return result;
    }
    public void importFromPreview(List<VocabularyCardPreviewDTO> items) {

        List<VocabularyCard> batch = new ArrayList<>();

        for (VocabularyCardPreviewDTO dto : items) {

            VocabularyCard card = new VocabularyCard();
            card.setWord(dto.getWord());
            card.setMeaning(dto.getMeaning());
            card.setPhonetic(dto.getPhonetic());
            card.setExample(dto.getExample());
            card.setExampleMeaning(dto.getExampleMeaning());
            card.setImageUrl(dto.getImageUrl());
            card.setAudioUrl(dto.getAudioUrl());

            if (dto.getTopicId() != null) {
                card.setTopic(
                        topicRepo.getReferenceById(dto.getTopicId())
                );
            }

            batch.add(card);
        }

        vocabularyRepo.saveAll(batch);
    }
    @Transactional
    public void update(Long vocabId, VocabularyUpdateRequest req) {

        VocabularyCard card = vocabularyRepo.findById(vocabId)
                .orElseThrow(() -> new RuntimeException("Vocabulary not found"));

        card.setWord(req.getWord());
        card.setMeaning(req.getMeaning());
        card.setPhonetic(req.getPhonetic());
        card.setExample(req.getExample());
        card.setExampleMeaning(req.getExampleMeaning());
        card.setImageUrl(req.getImageUrl());
        card.setAudioUrl(req.getAudioUrl());

        if (req.getTopicId() != null) {
            card.setTopic(topicRepo.getReferenceById(req.getTopicId()));
        }

        UnlockQuestion q = unlockQuestionRepository
                .findByCardId(vocabId)
                .orElse(null);

        if (q == null) {
            q = new UnlockQuestion();
            q.setCardId(vocabId);
        }

        q.setQuestion(req.getChoice().getQuestion());

        q.setCorrectIndex(
                switch (req.getChoice().getCorrect()) {
                    case "A" -> 0;
                    case "B" -> 1;
                    case "C" -> 2;
                    case "D" -> 3;
                    default -> 0;
                }
        );

        unlockQuestionRepository.save(q);

        List<UnlockQuestionOption> options =
                optionRepo.findByQuestionIdOrderByOptionIndex(q.getId());

        if (options.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                UnlockQuestionOption op = new UnlockQuestionOption();
                op.setQuestionId(q.getId());
                op.setOptionIndex(i);
                options.add(op);
            }
        }

        options.get(0).setOptionText(req.getChoice().getA());
        options.get(1).setOptionText(req.getChoice().getB());
        options.get(2).setOptionText(req.getChoice().getC());
        options.get(3).setOptionText(req.getChoice().getD());

        optionRepo.saveAll(options);
    }


    private String getString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private Long getLong(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        String v = cell.getStringCellValue().trim();
        return v.isEmpty() ? null : Long.valueOf(v);
    }

}

