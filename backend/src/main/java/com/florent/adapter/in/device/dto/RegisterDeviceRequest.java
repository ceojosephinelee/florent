package com.florent.adapter.in.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterDeviceRequest(
        @NotBlank String fcmToken,
        @NotNull String platform
) {}