package com.florent.adapter.out.push;

import com.florent.domain.notification.PushNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local")
public class MockFcmPushAdapter implements PushNotificationPort {

    @Override
    public void send(String fcmToken, String title, String body,
                     String type, String referenceType, Long referenceId) {
        log.info("[MockFCM] token={}, title={}, body={}, type={}, ref={}:{}",
                fcmToken, title, body, type, referenceType, referenceId);
    }
}