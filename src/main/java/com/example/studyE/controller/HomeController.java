package com.example.studyE.controller;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {
    private final HomeService homeService;

    @GetMapping()
    public List<TopicHomeResponse> home() {
        return homeService.getHomeTopics();
    }
}
