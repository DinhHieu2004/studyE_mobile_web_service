package com.example.studyE.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),

    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    LESSON_NOT_FOUND(2001, "Lession not found", HttpStatus.NOT_FOUND),
    TOPIC_NOT_FOUND(2002, "Topic not found", HttpStatus.NOT_FOUND),
    VOCABULARY_NOT_FOUND(2003, "Vocabulary not found", HttpStatus.NOT_FOUND),

    DIALOG_NOT_FOUND(3001, "Dialog not found", HttpStatus.NOT_FOUND),

    PROGRESS_NOT_FOUND(4001, "User progress not found", HttpStatus.NOT_FOUND),

    FIREBASE_TOKEN_INVALID(5001, "Invalid Firebase token", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String msg, HttpStatus httpStatus) {
        this.code = code;
        this.message = msg;
        this.httpStatus = httpStatus;
    }
}
