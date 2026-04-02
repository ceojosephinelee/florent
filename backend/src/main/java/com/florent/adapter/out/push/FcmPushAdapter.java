package com.florent.adapter.out.push;

import com.florent.domain.notification.PushNotificationPort;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@Component
@Profile("prod")
public class FcmPushAdapter implements PushNotificationPort {

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    void initFirebase() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);
        log.info("[FCM] Firebase Admin SDK 초기화 완료");
    }

    @Override
    public void send(String fcmToken, String title, String body) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            log.info("[FCM] 푸시 발송 성공: messageId={}", messageId);
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] 푸시 발송 실패: token={}, error={}", fcmToken, e.getMessage(), e);
        }
    }
}
