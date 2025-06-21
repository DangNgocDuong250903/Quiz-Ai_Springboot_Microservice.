package com.LinkVerse.identity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.LinkVerse.identity.dto.request.ApiResponse;
import com.LinkVerse.identity.dto.request.PermissionRequest;
import com.LinkVerse.identity.dto.response.PermissionResponse;
import com.LinkVerse.identity.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
 private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionResponse>> create(@RequestBody PermissionRequest request) {
        var response = permissionService.create(request);
        return ResponseEntity.ok(ApiResponse.<PermissionResponse>builder()
                .message("Permission created successfully")
                .result(response)
                .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAll() {
        var response = permissionService.getAll();
        return ResponseEntity.ok(ApiResponse.<List<PermissionResponse>>builder()
                .message("List of permissions")
                .result(response)
                .build());
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String name) {
        permissionService.delete(name);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Permission deleted")
                .build());
    }

    @PostMapping("/assign-to-user")
    public ResponseEntity<ApiResponse<Void>> assignPermissionToUser(
            @RequestParam String userId,
            @RequestParam String permissionName) {
        permissionService.assignPermissionToUser(userId, permissionName);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Permission assigned to user")
                .build());
    }
}
