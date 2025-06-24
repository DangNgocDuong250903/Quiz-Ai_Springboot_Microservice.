package com.LinkVerse.identity.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.LinkVerse.identity.dto.request.ApiResponse;
import com.LinkVerse.identity.dto.request.UserCreationRequest;
import com.LinkVerse.identity.dto.request.UserUpdateRequest;
import com.LinkVerse.identity.dto.request.UserUpdateRequestAdmin;
import com.LinkVerse.identity.dto.response.UserResponse;
import com.LinkVerse.identity.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateImage(@PathVariable("userId") String userId, @RequestParam String imageFile) {
        if (userId == null || userId.isEmpty() || imageFile == null || imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            userService.updateImage(userId, imageFile);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("User not found for ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Failed to update user image for ID: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/registration")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }
    
    @GetMapping
    ApiResponse<Page<UserResponse>> getUsers(@RequestParam int page, @RequestParam int size) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userService.getUsers(page, size))
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam String password) {
        userService.deleteUser(password);
        return ResponseEntity.ok(
                "User has been temporarily deleted. If you do not log in within 30 days, your account will be permanently deleted.");
    }

    @DeleteMapping("/delete-permanently")
    public ResponseEntity<String> deleteUserPermanently(@RequestParam String password) {
        userService.deleteUserPermanent(password);
        return ResponseEntity.ok("User has been deleted forever.");
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<String> deleteUserByAdmin(@PathVariable String userId) {
        userService.deleteUserByAdmin(userId);
        return ResponseEntity.ok("User has been permanently deleted by QTV.");
    }

    @PatchMapping("/my-profile/status")
    public ApiResponse<UserResponse> updateMyStatus(@RequestParam String status) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateStatus(userId, status))
                .build();
    }

    @PutMapping("/update/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequestAdmin request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @PatchMapping("/my-profile")
    ApiResponse<UserResponse> updateUserbyUsers(@RequestBody UserUpdateRequest request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserbyUsers(userId, request))
                .build();
    }
}
