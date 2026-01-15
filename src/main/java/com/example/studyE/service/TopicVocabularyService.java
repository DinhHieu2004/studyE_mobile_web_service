package com.example.studyE.service;

import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.TopicHomeResponse;
import com.example.studyE.entity.*;
import com.example.studyE.mapper.LessionMapper;
import com.example.studyE.repository.TopicVocabularyRepository;
import com.example.studyE.repository.UserRepository;
import com.example.studyE.repository.UserWatchedTopicVRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicVocabularyService {
    private final TopicVocabularyRepository topicVocabularyRepository;
    private final UserWatchedTopicVRepository userWatchedTopicVRepository;
    private final UserRepository userRepository;
    public List<TopicHomeResponse> getListTopicVocabulary() {
        return topicVocabularyRepository.findAll()
                .stream()
                .map(t -> new TopicHomeResponse(
                        t.getId(),
                        t.getName(),
                        t.getImagePath()
                ))
                .toList();
    }
    public List<TopicHomeResponse> getTopicWatched(Long userId) {
        List<TopicVocabulary> watchedTopic =
                userWatchedTopicVRepository.findWatchedTopicVsByUserId(userId);

        return watchedTopic.stream()
                .map(t -> new TopicHomeResponse(
                        t.getId(),
                        t.getName(),
                        t.getImagePath()
                ))
                .toList();
    }

    @Transactional
    public void markAsWatched(Long topicId, Long userId) {
        TopicVocabulary topic = topicVocabularyRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean alreadyWatched = userWatchedTopicVRepository.existsByTopic_IdAndUser_Id(topicId, userId);
        if (alreadyWatched) return;

        UserWatchedTopicV watched = UserWatchedTopicV.builder()
                .topic(topic)
                .user(user)
                .viewedAt(LocalDateTime.now())
                .build();
        userWatchedTopicVRepository.save(watched);

    }

}
