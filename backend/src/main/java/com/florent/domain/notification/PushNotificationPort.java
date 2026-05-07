package com.florent.domain.notification;

public interface PushNotificationPort {
    void send(String fcmToken, String title, String body,
              String type, String referenceType, Long referenceId);
}