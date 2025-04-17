package com.LinkVerse.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    CANNOT_SEND_EMAIL(1008, "Cannot send email", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1009, "User not found", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(1010, "Invalid token", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED(1011, "Token expired", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_FOUND(1012, "Token not found", HttpStatus.NOT_FOUND),
    EMAIL_SEND_FAILED(1013, "Email send failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_NOT_FOUND(1014, "Email not found", HttpStatus.NOT_FOUND),
    TOO_MANY_REQUESTS(1015, "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_OTP(1016, "Invalid OTP", HttpStatus.BAD_REQUEST);

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
