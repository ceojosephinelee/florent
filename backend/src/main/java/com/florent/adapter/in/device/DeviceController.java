package com.florent.adapter.in.device;

import com.florent.adapter.in.device.dto.RegisterDeviceRequest;
import com.florent.adapter.in.device.dto.RegisterDeviceResponse;
import com.florent.common.response.ApiResponse;
import com.florent.common.security.UserPrincipal;
import com.florent.domain.notification.DevicePlatform;
import com.florent.domain.notification.RegisterDeviceCommand;
import com.florent.domain.notification.RegisterDeviceResult;
import com.florent.domain.notification.RegisterDeviceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final RegisterDeviceUseCase registerDeviceUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<RegisterDeviceResponse>> registerDevice(
            @RequestBody @Valid RegisterDeviceRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        RegisterDeviceResult result = registerDeviceUseCase.register(
                new RegisterDeviceCommand(
                        principal.getUserId(),
                        request.fcmToken(),
                        DevicePlatform.valueOf(request.platform())));
        return ResponseEntity.ok(ApiResponse.success(RegisterDeviceResponse.from(result)));
    }
}