package com.example.studyE.controller;

import com.example.studyE.dto.request.TokenRequest;
import com.example.studyE.dto.response.UserResponse;
import com.example.studyE.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> loginWithToken(@RequestBody TokenRequest tokenRequest) throws FirebaseAuthException {
        try {
            UserResponse response = authService.loginWithToken(tokenRequest.getIdToken());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
}

