package com.LinkVerse.identity.entity;

import java.util.Objects;

import jakarta.persistence.Embeddable;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DeviceInfo {
    private String deviceId;
    private String deviceType;
    private String ipAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceInfo that = (DeviceInfo) o;
        return Objects.equals(deviceId, that.deviceId)
                && Objects.equals(deviceType, that.deviceType)
                && Objects.equals(ipAddress, that.ipAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, deviceType, ipAddress);
    }
}
