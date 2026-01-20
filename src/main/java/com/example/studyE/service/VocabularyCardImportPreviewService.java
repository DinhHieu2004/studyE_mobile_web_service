package com.example.studyE.service;

import com.example.studyE.dto.response.VocabularyCardPreviewDTO;
import com.example.studyE.entity.VocabularyCard;
import com.example.studyE.repository.TopicVocabularyRepository;
import com.example.studyE.repository.VocabularyCardRepository;
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

