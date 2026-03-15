package com.florent.domain.notification;

import com.florent.support.TestFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;

class UserDeviceTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @Test
    @DisplayName("register() — isActive true이고 필드가 올바르게 설정된다")
    void register_정상_생성() {
        // given & when
        UserDevice device = UserDevice.register(
                1L, DevicePlatform.IOS, "fcm-token-123", fixedClock);

        // then
        assertThat(device.getUserId()).isEqualTo(1L);
        assertThat(device.getPlatform()).isEqualTo(DevicePlatform.IOS);
        assertThat(device.getFcmToken()).isEqualTo("fcm-token-123");
        assertThat(device.isActive()).isTrue();
    }

    @Test
    @DisplayName("updateToken() — 토큰과 플랫폼이 갱신되고 isActive가 true이다")
    void updateToken_정상_갱신() {
        // given
        UserDevice device = UserDevice.register(
                1L, DevicePlatform.IOS, "old-token", fixedClock);

        // when
        device.updateToken("new-token", DevicePlatform.ANDROID, fixedClock);

        // then
        assertThat(device.getFcmToken()).isEqualTo("new-token");
        assertThat(device.getPlatform()).isEqualTo(DevicePlatform.ANDROID);
        assertThat(device.isActive()).isTrue();
    }
}