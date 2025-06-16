package com.example.studyE.service;

import com.example.studyE.dto.request.SentencePartRequest;
import com.example.studyE.dto.response.SentencePartResponse;
import com.example.studyE.dto.response.SentenceResponse;
import com.example.studyE.entity.Sentence;
import com.example.studyE.mapper.SentenceMapper;
import com.example.studyE.repository.SentenceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SentenceService {

    SentenceRepository sentenceRepository;

    SentenceMapper sentenceMapper;

    public SentencePartResponse getPartList(String level) {
        try {
            List<Integer> partNumbers = sentenceRepository.findAllByLevel(level)
                    .stream()
                    .map(Sentence::getPartNumber)
                    .filter(part -> part != null)
                    .distinct()
                    .toList();


            return SentencePartResponse.builder()
                    .parts(partNumbers.stream()
                            .map(String::valueOf)
                            .collect(Collectors.toList())).build();

        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách part cho trình độ {}: ", level, e);
            throw new RuntimeException("Không thể lấy danh sách part cho trình độ " + level);
        }
    }

    public List<SentenceResponse> getSentenceByPartAndLevel(String level, Integer part) {


        List<Sentence> list = sentenceRepository.findAllByLevelAndPartNumber(level, part);


       // return list.stream().map(sentenceMapper::toResponse).collect(Collectors.toList());
        return list.stream()
                .map(sentence -> {
                    SentenceResponse response = sentenceMapper.toResponse(sentence);
                    response.setContent(cutNumberSentence(response.getContent()));
                    return response;
                })
                .collect(Collectors.toList());
    }
    private String cutNumberSentence(String sentence) {
        return sentence.substring(3);
    }
}
