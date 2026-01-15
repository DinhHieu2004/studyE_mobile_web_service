package com.example.studyE.service;

import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final TopicRepository topicRepository;

    public List<TopicHomeResponse> getHomeTopics() {
        return topicRepository.findTopicsForHome();
    }
}

