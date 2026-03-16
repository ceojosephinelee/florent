package com.florent.adapter.out.push;

import com.florent.domain.notification.PushNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MockFcmPushAdapter implements PushNotificationPort {

    @Override
    public void send(String fcmToken, String title, String body) {
        log.info("[MockFCM] token={}, title={}, body={}", fcmToken, title, body);
    }
}