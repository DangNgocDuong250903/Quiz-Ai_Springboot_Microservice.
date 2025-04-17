package com.LinkVerse.identity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL(1009, "Invalid email", HttpStatus.BAD_REQUEST),
    PROFILE_CREATION_FAILED(1010, "Profile creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED_ACCESS(1011, "Unauthorized access", HttpStatus.FORBIDDEN),
    GROUP_NOT_EXISTED(1012, "Group not existed", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(1013, "Invalid token", HttpStatus.BAD_REQUEST),
    MEMBER_NOT_FOUND(1014, "Member not found", HttpStatus.NOT_FOUND),
    GROUP_ALREADY_EXISTS(1015, "Group already exists", HttpStatus.BAD_REQUEST),
    USER_ALREADY_MEMBER(1016, "User already a member", HttpStatus.BAD_REQUEST),
    GROUP_NOT_EXIST(1017, "Group not exist", HttpStatus.NOT_FOUND),
    PERMISSION_DENIED(1018, "Permission denied", HttpStatus.FORBIDDEN),
    ALREADY_MEMBER(1019, "Already a member", HttpStatus.BAD_REQUEST),
    GROUP_NOT_FOUND(1020, "Group not found", HttpStatus.NOT_FOUND),
    USER_NOT_IN_GROUP(1021, "User not in group", HttpStatus.FORBIDDEN),
    STORY_NOT_EXISTED(1022, "Story not existed", HttpStatus.NOT_FOUND),
    FORBIDDEN(1023, "Forbidden", HttpStatus.FORBIDDEN),
    JSON_PROCESSING_FAILED(1024, "JSON processing failed", HttpStatus.INTERNAL_SERVER_ERROR),
    POST_CREATION_FAILED(1025, "Post creation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_DELETED(1026, "User deleted", HttpStatus.BAD_REQUEST),
    PROFILE_DELETION_FAILED(1027, "Profile deletion failed", HttpStatus.INTERNAL_SERVER_ERROR),
    OTP_SENT_FAILED(1028, "OTP sent failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_OTP(1029, "Invalid or expired OTP", HttpStatus.BAD_REQUEST),
    OTP_SENT(1030, "OTP sent ", HttpStatus.OK),
    EMAIL_NOT_FOUND(1031, "Email not found", HttpStatus.NOT_FOUND),
    OTP_GENERATION_FAILED(1032, "OTP generation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PHONE_NUMBER(1033, "Invalid phone number", HttpStatus.BAD_REQUEST),
    TOO_MANY_REQUESTS(1034, "Too many requests", HttpStatus.TOO_MANY_REQUESTS),
    WAIT_BEFORE_RETRY(1035, "Please wait 3 minutes before requesting another OTP", HttpStatus.TOO_MANY_REQUESTS),
    MAX_LEADERS_REACHED(1036, "Max leaders reached", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_CHANGE(1037, "Invalid role change", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_ALREADY_EXISTS(1038, "Phone number already exists", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1039, "Email already exists", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(1040, "Username already exists", HttpStatus.BAD_REQUEST),
    INVALID_CHAT_ID(1038, "Invalid chat id", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ADD(1039, "Unauthorized add user", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_RENAME(1040, "Unauthorized rename user", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_REMOVE(1041, "Unauthorized remove user", HttpStatus.BAD_REQUEST),
    NOT_RELATE_CHAT(1042, "User not relate to this chat", HttpStatus.BAD_REQUEST),
    INVALID_MESSAGE_ID(1043, "Message can't be found", HttpStatus.BAD_REQUEST),
    INCORRECT_PASSWORD(1044, "Incorrect password", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(1045, "Password mismatch", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_MUST_DIFFERENT(1046, "New password must be different from old password", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_MAX_USAGE_EXCEEDED(1047, "Phone number max usage exceeded", HttpStatus.BAD_REQUEST),
    LOGIN_NOTIFICATION_FAILED(1048, "Login notification failed", HttpStatus.INTERNAL_SERVER_ERROR),
    MSSV_ALREADY_EXISTS(1049, "MSSV already exists", HttpStatus.BAD_REQUEST),
    NOT_FOUND(1050, "Not found", HttpStatus.NOT_FOUND),
    QUIZ_ALREADY_SUBMITTED (1051, "Quiz already submitted", HttpStatus.BAD_REQUEST),
    ALREADY_EXISTED(1052, "Already existed", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
