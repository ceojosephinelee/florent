package com.florent.application.notification;

import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationRepository;
import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxEventRepository;
import com.florent.domain.notification.PushNotificationPort;
import com.florent.domain.notification.UserDevice;
import com.florent.domain.notification.UserDeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorker {

    private static final int BATCH_SIZE = 50;

    private final OutboxEventRepository outboxEventRepository;
    private final NotificationRepository notificationRepository;
    private final UserDeviceRepository userDeviceRepository;
    private final PushNotificationPort pushNotificationPort;
    private final Clock clock;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutbox() {
        List<OutboxEvent> events = outboxEventRepository.findPendingBefore(
                LocalDateTime.now(clock), BATCH_SIZE);

        log.info("OutboxWorker 실행: {} 건 PENDING", events.size());

        for (OutboxEvent event : events) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        try {
            Notification notification = notificationRepository.findById(event.getNotificationId())
                    .orElse(null);
            if (notification == null) {
                log.warn("Notification not found: id={}", event.getNotificationId());
                event.incrementAttemptAndRetry(clock);
                outboxEventRepository.save(event);
                return;
            }

            List<UserDevice> devices = userDeviceRepository.findActiveByUserId(
                    notification.getUserId());
            if (devices.isEmpty()) {
                log.info("No active devices for userId={}", notification.getUserId());
                event.markSent();
                outboxEventRepository.save(event);
                return;
            }

            for (UserDevice device : devices) {
                pushNotificationPort.send(
                        device.getFcmToken(),
                        notification.getTitle(),
                        notification.getBody());
            }

            event.markSent();
            outboxEventRepository.save(event);
            log.info("OutboxEvent sent: id={}, notificationId={}",
                    event.getId(), event.getNotificationId());
        } catch (Exception e) {
            log.error("OutboxEvent processing failed: id={}", event.getId(), e);
            event.incrementAttemptAndRetry(clock);
            outboxEventRepository.save(event);
        }
    }
}
