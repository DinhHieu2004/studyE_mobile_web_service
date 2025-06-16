package com.example.studyE.controller;


import com.example.studyE.dto.response.DialogResponse;
import com.example.studyE.entity.Dialog;
import com.example.studyE.repository.DialogRepository;
import com.example.studyE.service.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/dialogs")
public class DialogController {

    @Autowired
    private DialogService dialogService;
    @GetMapping("/{lessionId}")
    public List<DialogResponse> getDialogsByLessionId(@PathVariable Long lessionId) {
        return dialogService.getDialogByLessionId(lessionId);
    }
}
