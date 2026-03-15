package com.florent.application.notification;

import com.florent.domain.notification.DevicePlatform;
import com.florent.domain.notification.RegisterDeviceCommand;
import com.florent.domain.notification.RegisterDeviceResult;
import com.florent.fake.FakeUserDeviceRepository;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceServiceTest {

    private FakeUserDeviceRepository userDeviceRepository;
    private DeviceService sut;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        userDeviceRepository = new FakeUserDeviceRepository();
        sut = new DeviceService(userDeviceRepository, fixedClock);
    }

    @Test
    @DisplayName("새 디바이스를 등록한다")
    void register_신규_디바이스() {
        // given
        RegisterDeviceCommand command = new RegisterDeviceCommand(
                1L, "fcm-token-abc", DevicePlatform.IOS);

        // when
        RegisterDeviceResult result = sut.register(command);

        // then
        assertThat(result.deviceId()).isNotNull();
    }

    @Test
    @DisplayName("기존 토큰이 있으면 업데이트한다")
    void register_기존_토큰_업데이트() {
        // given
        sut.register(new RegisterDeviceCommand(1L, "same-token", DevicePlatform.IOS));

        // when
        RegisterDeviceResult result = sut.register(
                new RegisterDeviceCommand(1L, "same-token", DevicePlatform.ANDROID));

        // then — id가 동일 (업데이트)
        assertThat(result.deviceId()).isEqualTo(1L);
    }
}