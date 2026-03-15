package com.florent.adapter.in.device.dto;

import com.florent.domain.notification.RegisterDeviceResult;

public record RegisterDeviceResponse(Long deviceId) {
    public static RegisterDeviceResponse from(RegisterDeviceResult result) {
        return new RegisterDeviceResponse(result.deviceId());
    }
}