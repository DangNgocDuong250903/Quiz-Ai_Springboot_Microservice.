package com.LinkVerse.identity.dto.request;

import java.time.LocalDateTime;

import com.LinkVerse.identity.entity.DeviceInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryRequest {
    private Long id;
    private LocalDateTime loginTime;
    private DeviceInfo deviceInfo;
}
