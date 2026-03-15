package com.florent.domain.notification;

public record RegisterDeviceResult(Long deviceId) {
    public static RegisterDeviceResult from(UserDevice device) {
        return new RegisterDeviceResult(device.getId());
    }
}