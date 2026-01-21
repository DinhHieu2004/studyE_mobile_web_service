package com.example.studyE.service.impl;

import com.example.studyE.dto.request.LessionRequest;
import com.example.studyE.dto.response.LessionResponse;
import com.example.studyE.dto.response.PageResponse;
import com.example.studyE.entity.*;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
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

        var now = LocalDateTime.now();

        UserWatchedLesson progress = userWatchedLessonRepository
                .findByLesson_IdAndUser_Id(lessonId, userId)
                .orElse(null);

        if (progress == null) {
            progress = UserWatchedLesson.builder()
                    .lesson(lesson)
                    .user(user)
                    .viewedAt(now)
                    .status(ProgressStatus.IN_PROGRESS)
                    .updatedAt(now)
                    .build();
        } else {
            progress.setUpdatedAt(now);
            if (progress.getStatus() == null) progress.setStatus(ProgressStatus.IN_PROGRESS);
            if (progress.getViewedAt() == null) progress.setViewedAt(now);
        }

        userWatchedLessonRepository.save(progress);
    }

    @Transactional
    @Override
    public void markAsDone(Long lessonId, Long userId) {
        Lession lesson = lessionRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var now = LocalDateTime.now();

        UserWatchedLesson progress = userWatchedLessonRepository
                .findByLesson_IdAndUser_Id(lessonId, userId)
                .orElse(null);

        if (progress == null) {
            progress = UserWatchedLesson.builder()
                    .lesson(lesson)
                    .user(user)
                    .viewedAt(now)
                    .status(ProgressStatus.DONE)
                    .completedAt(now)
                    .updatedAt(now)
                    .build();
        } else {
            progress.setStatus(ProgressStatus.DONE);
            if (progress.getViewedAt() == null) progress.setViewedAt(now);
            progress.setCompletedAt(now);
            progress.setUpdatedAt(now);
        }

        userWatchedLessonRepository.save(progress);
    }

    @Override
    public List<LessionResponse> getLessonsDoneHistory(Long userId) {
        List<UserWatchedLesson> rows = userWatchedLessonRepository.findDoneHistory(userId);

        return rows.stream().map(uwl -> {
            Lession lesson = uwl.getLesson();

            LessionResponse res = LessionMapper.toDto(lesson);

            res.setProgressStatus("DONE");
            res.setCompletedAt(uwl.getCompletedAt());

            return res;
        }).toList();
    }

    @Override
    public PageResponse<LessionResponse> getLessionsForAdmin(
            Long topicId, String q, String status, Boolean premium, String level, int page, int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Lession> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (topicId != null) {
                predicates.add(cb.equal(root.get("topic").get("id"), topicId));
            }
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }
            if (premium != null) {
                predicates.add(cb.equal(root.get("isPremium"), premium));
            }
            if (level != null && !level.isBlank()) {
                predicates.add(cb.equal(root.get("level"), level.trim()));
            }
            if (q != null && !q.isBlank()) {
                String kw = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), kw),
                        cb.like(cb.lower(root.get("description")), kw),
                        cb.like(cb.lower(root.get("topic").get("name")), kw)
                ));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Lession> p = lessionRepository.findAll(spec, pageable);

        List<LessionResponse> items = p.getContent()
                .stream()
                .map(LessionMapper::toDto)
                .toList();

        return PageResponse.<LessionResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalPage(p.getTotalPages())
                .totalItems(p.getTotalElements())
                .isLast(p.isLast())
                .items(items)
                .build();
    }

    @Override
    public LessionResponse getLessionByIdForAdmin(Long id) {
        Lession lession = lessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND, "Lession not found with id = " + id));
        return LessionMapper.toDto(lession);
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
        lession.setPremium(Boolean.TRUE.equals(request.getPremium()));

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
    public LessionResponse getLessionById(Long id, Long userId) {
        Lession lession = lessionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND, "Lession not found with id = " + id));

        LessionResponse dto = LessionMapper.toDto(lession);

        if (userId != null) {
            userWatchedLessonRepository.findByLesson_IdAndUser_Id(id, userId).ifPresent(p -> {
                dto.setProgressStatus(p.getStatus().name());
                dto.setCompletedAt(p.getCompletedAt());
            });
        }

        return dto;
    }


    @Override
    public PageResponse<LessionResponse> getLessions(Long topicId, int page, int size, Long userId) {
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

        List<Lession> lessons = lessionPage.getContent();

        Map<Long, UserWatchedLesson> progressMap;
        if (userId != null && !lessons.isEmpty()) {
            List<Long> ids = lessons.stream().map(Lession::getId).toList();
            progressMap = userWatchedLessonRepository
                    .findByUser_IdAndLesson_IdIn(userId, ids)
                    .stream()
                    .collect(Collectors.toMap(
                            p -> p.getLesson().getId(),
                            Function.identity(),
                            (a, b) -> a
                    ));
        } else {
            progressMap = Collections.emptyMap();
        }

        List<LessionResponse> items = lessons.stream().map(lesson -> {
            LessionResponse dto = LessionMapper.toDto(lesson);

            UserWatchedLesson p = progressMap.get(lesson.getId());
            if (p != null) {
                dto.setProgressStatus(p.getStatus().name());
                dto.setCompletedAt(p.getCompletedAt());
            }
            return dto;
        }).toList();

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
