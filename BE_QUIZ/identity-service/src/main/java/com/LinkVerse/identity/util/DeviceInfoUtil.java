package com.LinkVerse.identity.util;

import jakarta.servlet.http.HttpServletRequest;

import com.LinkVerse.identity.entity.DeviceInfo;

import ua_parser.Client;
import ua_parser.Parser;

public class DeviceInfoUtil {

    private static final Parser uaParser = new Parser();

    public static DeviceInfo extractDeviceInfo(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null || userAgentString.isEmpty()) {
            userAgentString = "Unknown User-Agent";
        }

        Client client = uaParser.parse(userAgentString);

        String deviceFamily = client.device.family;
        String osFamily = client.os.family;
        String ipAddress = request.getRemoteAddr();

        String deviceType = deviceFamily.equals("Other") || deviceFamily.equals("Unknown")
                ? inferDeviceType(osFamily, userAgentString)
                : deviceFamily;

        return DeviceInfo.builder()
                .deviceId(userAgentString.hashCode() + "")
                .deviceType(deviceType)
                .ipAddress(ipAddress)
                .build();
    }

    private static String inferDeviceType(String osFamily, String userAgentString) {
        if (osFamily == null) osFamily = "";
        osFamily = osFamily.toLowerCase();

        if (osFamily.contains("android")) {
            return "Android Device";
        } else if (osFamily.contains("ios")) {
            return "iOS Device";
        } else if (osFamily.contains("windows")) {
            return "Windows Device";
        } else if (osFamily.contains("mac os")) {
            return "Mac Device";
        } else if (osFamily.contains("linux")) {
            return "Linux Device";
        }

        if (userAgentString.toLowerCase().contains("mobile")) {
            return "Mobile Device";
        } else if (userAgentString.toLowerCase().contains("tablet")) {
            return "Tablet";
        }

        return "Unknown Device";
    }
}
