package com.example.studyE.service;

import com.example.studyE.dto.response.DialogResponse;

import java.util.List;

public interface DialogService {
    List<DialogResponse> getDialogByLessionId(Long lessionId);
}
