package com.florent.application.notification;

import com.florent.domain.notification.RegisterDeviceCommand;
import com.florent.domain.notification.RegisterDeviceResult;
import com.florent.domain.notification.RegisterDeviceUseCase;
import com.florent.domain.notification.UserDevice;
import com.florent.domain.notification.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService implements RegisterDeviceUseCase {

    private final UserDeviceRepository userDeviceRepository;
    private final Clock clock;

    @Override
    public RegisterDeviceResult register(RegisterDeviceCommand command) {
        UserDevice device = userDeviceRepository.findByFcmToken(command.fcmToken())
                .map(existing -> {
                    existing.updateToken(command.fcmToken(), command.platform(), clock);
                    return existing;
                })
                .orElseGet(() -> UserDevice.register(
                        command.userId(), command.platform(),
                        command.fcmToken(), clock));

        UserDevice saved = userDeviceRepository.save(device);
        return RegisterDeviceResult.from(saved);
    }
}