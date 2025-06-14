package com.example.studyE.service.impl;

import com.example.studyE.dto.response.DialogResponse;
import com.example.studyE.repository.DialogRepository;
import com.example.studyE.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DialogServiceImpl implements DialogService {

    @Autowired
    private DialogRepository dialogRepository;

    @Override
    public List<DialogResponse> getDialogByLessionId(Long lessionId) {
        return dialogRepository.findByLessionIdOrderByIdAsc(lessionId).stream()
                .map(dialog -> {
                    DialogResponse res = new DialogResponse();
                    res.setSpeaker(dialog.getSpeaker());
                    res.setContent(dialog.getContent());
                    res.setAudioUrl(dialog.getAudioUrl());
                    return res;
                }).collect(Collectors.toList());
    }
}
