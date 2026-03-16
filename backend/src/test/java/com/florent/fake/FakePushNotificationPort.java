package com.florent.fake;

import com.florent.domain.notification.PushNotificationPort;

import java.util.ArrayList;
import java.util.List;

public class FakePushNotificationPort implements PushNotificationPort {

    private final List<PushRecord> sentMessages = new ArrayList<>();
    private boolean shouldFail = false;

    @Override
    public void send(String fcmToken, String title, String body) {
        if (shouldFail) {
            throw new RuntimeException("FCM send failed");
        }
        sentMessages.add(new PushRecord(fcmToken, title, body));
    }

    public List<PushRecord> getSentMessages() {
        return List.copyOf(sentMessages);
    }

    public void setShouldFail(boolean shouldFail) {
        this.shouldFail = shouldFail;
    }

    public record PushRecord(String fcmToken, String title, String body) {}
}
