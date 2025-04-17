package com.LinkVerse.identity.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.LinkVerse.identity.dto.request.ApiResponse;
import com.LinkVerse.identity.dto.request.LoginHistoryRequest;
import com.LinkVerse.identity.dto.response.PageResponse;
import com.LinkVerse.identity.entity.LoginHistory;
import com.LinkVerse.identity.mapper.LoginHistoryMapper;
import com.LinkVerse.identity.service.LoginHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/login-history")
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;
    private final LoginHistoryMapper loginHistoryMapper;

    @GetMapping("/me")
    public ApiResponse<PageResponse<LoginHistoryRequest>> getMyLoginHistory(
            @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {

        if (page < 1 || size < 1) {
            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Page and size 1 ")
                    .build();
        }

        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<LoginHistory> pageData = loginHistoryService.getCurrentUserLoginHistory(pageable);

            List<LoginHistoryRequest> dtoList = loginHistoryMapper.toDto(pageData.getContent());

            PageResponse<LoginHistoryRequest> pageResponse = PageResponse.<LoginHistoryRequest>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPage(pageData.getTotalPages())
                    .totalElement(pageData.getTotalElements())
                    .data(dtoList)
                    .build();

            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.OK.value())
                    .message("History successfully")
                    .result(pageResponse)
                    .build();
        } catch (Exception ex) {
            log.error("Failed login history", ex);
            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error login history.")
                    .build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse<LoginHistoryRequest>> getUserLoginHistory(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 1 || size < 1) {
            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Page and size 1")
                    .build();
        }

        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<LoginHistory> pageData = loginHistoryService.getUserLoginHistory(userId, pageable);

            List<LoginHistoryRequest> dtoList = loginHistoryMapper.toDto(pageData.getContent());

            PageResponse<LoginHistoryRequest> pageResponse = PageResponse.<LoginHistoryRequest>builder()
                    .currentPage(page)
                    .pageSize(size)
                    .totalPage(pageData.getTotalPages())
                    .totalElement(pageData.getTotalElements())
                    .data(dtoList)
                    .build();

            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.OK.value())
                    .message("Login history successfully")
                    .result(pageResponse)
                    .build();
        } catch (Exception ex) {
            log.error("Failed login history for user: {}", userId, ex);
            return ApiResponse.<PageResponse<LoginHistoryRequest>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Error login history ")
                    .build();
        }
    }
}
