package com.example.studyE.service;

import com.example.studyE.dto.request.PostUserRequest;

import com.example.studyE.dto.response.UserResponse;

import com.example.studyE.dto.request.TokenRequest;
import com.example.studyE.dto.response.AuthenResponse;
import com.example.studyE.entity.User;

import com.example.studyE.exception.AppException;
import com.example.studyE.exception.ErrorCode;
import com.example.studyE.repository.UserRepository;
import com.example.studyE.util.JwtUtil;
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
    UserRepository userRepository;
    JwtUtil jwtUtil;

    public AuthenResponse loginWithToken(TokenRequest tokenRequest) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(tokenRequest.getIdToken());
        String uid = decodedToken.getUid();
        Optional<User> optionalUser = userRepository.findByUid(uid);
        User user;

        if (optionalUser.isEmpty()) {
            user = new User();
            user.setUid(uid);
            user.setEmail(decodedToken.getEmail());
            user.setName(decodedToken.getName());

            userRepository.save(user);
        } else {
            user = optionalUser.get();
        }
        String token = jwtUtil.generateToken(user);



        log.info("token: {}", token);

        return AuthenResponse.builder()
                .token(token)
                .uid(user.getUid())
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

    public UserResponse getUserByUid(String uid) {
        Optional<User> userOpt = userRepository.findByUid(uid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new UserResponse(
                    user.getEmail(),
                    user.getName(),
                    user.getUid(),
                    user.getPhone(),
                    user.getDob(),
                    user.getSubscriptionPlan()
            );
        }
        return null;
    }

    public void updateUserProfile(UserResponse userDto) {
        Optional<User> optional = userRepository.findByUid(userDto.getUid());
        if (optional.isPresent()) {
            User user = optional.get();
            user.setEmail(userDto.getEmail());
            user.setName(userDto.getName());
            user.setDob(userDto.getDob());
            user.setPhone(userDto.getPhone());
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with UID: " + userDto.getUid());
        }
    }

    public void updateSubscription(String uid, String plan) {
        System.out.println("Updating subscription for UID: " + uid + ", plan: " + plan); // log
        Optional<User> userOpt = userRepository.findByUid(uid);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setSubscriptionPlan(plan);
            userRepository.save(user);
            System.out.println("Updated user: " + user.getSubscriptionPlan()); // log
        } else {
            throw new RuntimeException("User not found for subscription update");
        }
    }


}
