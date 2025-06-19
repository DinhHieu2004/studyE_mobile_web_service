package com.example.studyE.controller;

import com.example.studyE.dto.request.PostUserRequest;
import com.example.studyE.dto.request.TokenRequest;
import com.example.studyE.dto.response.AuthenResponse;
import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenResponse> loginWithToken(@RequestBody TokenRequest tokenRequest) throws FirebaseAuthException {
            return ResponseEntity.ok(authService.loginWithToken(tokenRequest));

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody PostUserRequest request) {
        try {
            log.info("Registering user with email: {}, uid: {}, name: {}",
                    request.getEmail(), request.getUid(), request.getName());
            authService.registerFirebaseUser(request);
            return ResponseEntity.ok("Đăng ký thành công");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống: " + e.getMessage());
        }
    }
    @GetMapping("/info")
    public ResponseEntity<?> getUserProfile(@RequestParam String uid){
        UserResponse user = authService.getUserByUid(uid);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy người dùng với uid: " + uid);
        }

        return ResponseEntity.ok(user);
    }
    @PostMapping("/updateInfo")
    public ResponseEntity<?> updateProfile(@RequestBody UserResponse userDto) {
        try {
            authService.updateUserProfile(userDto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/updateSubscription")
    public ResponseEntity<?> updateSubscription(@RequestParam String uid, @RequestParam String plan) {
        System.out.println(uid + plan);
        try {
            authService.updateSubscription(uid, plan);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

