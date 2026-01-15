package com.example.studyE.service.impl;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.entity.Lession;
import com.example.studyE.entity.Topic;
import com.example.studyE.entity.User;
import com.example.studyE.entity.UserWatchedLesson;
import com.example.studyE.exception.AppException;
import com.example.studyE.exception.ErrorCode;
import com.example.studyE.mapper.LessionMapper;
import com.example.studyE.repository.LessionRepository;
import com.example.studyE.repository.TopicRepository;
import com.example.studyE.repository.UserRepository;
import com.example.studyE.repository.UserWatchedLessonRepository;
import com.example.studyE.service.LessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessionServiceImpl implements LessionService {

    private final LessionRepository lessionRepository;
    private final TopicRepository topicRepository;
    private final UserWatchedLessonRepository userWatchedLessonRepository;
    private final UserRepository userRepository;

    @Override
    public PageResponse<LessionResponse> getLessionsByTopic(Long topicId, int page, int size) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND, "Topic not found with id = " + topicId));

        Pageable pageable = PageRequest.of(page, size);
        Page<Lession> lessionPage = lessionRepository.findAllByTopic(topic, pageable);

        List<LessionResponse> items = lessionPage.getContent()
                .stream()
                .map(LessionMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<LessionResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(lessionPage.getTotalPages())
                .totalItems(lessionPage.getTotalElements())
                .isLast(lessionPage.isLast())
                .items(items)
                .build();
    }

    @Override
    public List<LessionResponse> getAllLessions() {
        return lessionRepository.findAll()
                .stream()
                .map(LessionMapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public boolean existsById(Long id) {
        return lessionRepository.existsById(id);
    }

    @Override
    public List<LessionResponse> getLessonsWatched(Long userId) {
        List<Lession> watchedLessons = userWatchedLessonRepository.findWatchedLessonsByUserId(userId);

        return watchedLessons.stream()
                .map(LessionMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    @Override
    public void markAsWatched(Long lessonId, Long userId) {
        Lession lesson = lessionRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean alreadyWatched = userWatchedLessonRepository.existsByLesson_IdAndUser_Id(lessonId, userId);
        if (alreadyWatched) return;

        UserWatchedLesson watched = UserWatchedLesson.builder()
                .lesson(lesson)
                .user(user)
                .viewedAt(LocalDateTime.now())
                .build();
        userWatchedLessonRepository.save(watched);

    }

    @Override
    public LessionResponse getLessionById(Long id) {
        Lession lession = lessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND, "Lession not found with id = " + id));

        return LessionMapper.toDto(lession);
    }

    @Override
    public LessionResponse createLession(LessionRequest request) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND, "Topic not found with id = " + request.getTopicId()));

        Lession lession = LessionMapper.toEntity(request);
        lession.setTopic(topic);
        lessionRepository.save(lession);

        return LessionMapper.toDto(lession);
    }

    @Override
    public LessionResponse updateLession(Long id, LessionRequest request) {
        Lession lession = lessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND, "Lession not found with id = " + id));

        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new AppException(ErrorCode.TOPIC_NOT_FOUND, "Topic not found with id = " + request.getTopicId()));

        lession.setTitle(request.getTitle());
        lession.setDescription(request.getDescription());
        lession.setLevel(request.getLevel());
        lession.setImageUrl(request.getImageUrl());
        lession.setTopic(topic);

        lessionRepository.save(lession);

        return LessionMapper.toDto(lession);

    }

    @Override
    public void deleteLession(Long id) {
        if (!lessionRepository.existsById(id)) {
            throw new AppException(ErrorCode.LESSON_NOT_FOUND, "Lession not found with id = " + id);
        }
        lessionRepository.deleteById(id);
    }

    @Override
    public PageResponse<LessionResponse> getLessions(Long topicId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Lession> lessionPage;

        if (topicId == null) {
            lessionPage = lessionRepository.findAll(pageable);
        } else {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new AppException(
                            ErrorCode.TOPIC_NOT_FOUND,
                            "Topic not found with id = " + topicId
                    ));
            lessionPage = lessionRepository.findAllByTopic(topic, pageable);
        }

        List<LessionResponse> items = lessionPage.getContent()
                .stream()
                .map(LessionMapper::toDto)
                .toList();

        return PageResponse.<LessionResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(lessionPage.getTotalPages())
                .totalItems(lessionPage.getTotalElements())
                .isLast(lessionPage.isLast())
                .items(items)
                .build();
    }
}
