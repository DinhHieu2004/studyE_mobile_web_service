package com.example.studyE.service;

import com.example.studyE.dto.request.PostUserRequest;
import com.example.studyE.entity.User;

import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.exception.AppException;
import com.example.studyE.exception.ErrorCode;
import com.example.studyE.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthService {
    private UserRepository userRepository;

    public UserResponse loginWithToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        Optional<User> optionalUser = userRepository.findByUid(uid);
        User user = optionalUser.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return UserResponse.builder()
                .userId(user.getId())
                .uid(user.getUid())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public void registerFirebaseUser(PostUserRequest request) {
        if (request.getUid() == null || request.getEmail() == null || request.getName() == null) {
            throw new IllegalArgumentException("Thông tin đăng ký không đầy đủ");
        }

        if (userRepository.existsByUid(request.getUid())) {
            throw new IllegalArgumentException("Người dùng đã tồn tại");
        }

        User user = new User();
        user.setUid(request.getUid());
        user.setEmail(request.getEmail());
        user.setName(request.getName());

        userRepository.save(user);
    }
}
