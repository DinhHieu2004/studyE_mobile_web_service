package com.example.studyE.service;

import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.entity.User;
import com.example.studyE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j

public class UserService {
    UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> UserResponse.builder()
                        .uid(user.getUid())
                        .uid(user.getUid())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build())
                .toList();
    }
}
