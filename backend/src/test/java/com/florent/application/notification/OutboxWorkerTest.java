package com.florent.application.notification;

import com.florent.domain.notification.DevicePlatform;
import com.florent.domain.notification.Notification;
import com.florent.domain.notification.NotificationType;
import com.florent.domain.notification.OutboxEvent;
import com.florent.domain.notification.OutboxStatus;
import com.florent.domain.notification.ReferenceType;
import com.florent.domain.notification.UserDevice;
import com.florent.fake.FakeNotificationRepository;
import com.florent.fake.FakeOutboxEventRepository;
import com.florent.fake.FakePushNotificationPort;
import com.florent.fake.FakeUserDeviceRepository;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxWorkerTest {

    private FakeOutboxEventRepository outboxEventRepository;
    private FakeNotificationRepository notificationRepository;
    private FakeUserDeviceRepository userDeviceRepository;
    private FakePushNotificationPort pushNotificationPort;
    private OutboxWorker sut;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        outboxEventRepository = new FakeOutboxEventRepository();
        notificationRepository = new FakeNotificationRepository();
        userDeviceRepository = new FakeUserDeviceRepository();
        pushNotificationPort = new FakePushNotificationPort();
        sut = new OutboxWorker(
                outboxEventRepository, notificationRepository,
                userDeviceRepository, pushNotificationPort, fixedClock);
    }

    @Test
    @DisplayName("PENDING OutboxEvent를 처리하고 FCM 발송 후 SENT로 전이한다")
    void processOutbox_정상_발송() {
        // given
        Notification notification = saveNotification(100L);
        saveDevice(100L, "token-abc");
        saveOutboxEvent(notification.getId());

        // when
        sut.processOutbox();

        // then
        assertThat(pushNotificationPort.getSentMessages()).hasSize(1);
        assertThat(pushNotificationPort.getSentMessages().get(0).fcmToken())
                .isEqualTo("token-abc");

        OutboxEvent processed = outboxEventRepository.findAll().get(0);
        assertThat(processed.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    @DisplayName("활성 디바이스가 없으면 FCM 발송 없이 SENT로 처리한다")
    void processOutbox_디바이스_없음_SENT() {
        // given
        Notification notification = saveNotification(100L);
        saveOutboxEvent(notification.getId());

        // when
        sut.processOutbox();

        // then
        assertThat(pushNotificationPort.getSentMessages()).isEmpty();

        OutboxEvent processed = outboxEventRepository.findAll().get(0);
        assertThat(processed.getStatus()).isEqualTo(OutboxStatus.SENT);
    }

    @Test
    @DisplayName("FCM 발송 실패 시 attemptCount 증가 후 PENDING 유지한다")
    void processOutbox_발송_실패_재시도() {
        // given
        Notification notification = saveNotification(100L);
        saveDevice(100L, "token-abc");
        saveOutboxEvent(notification.getId());
        pushNotificationPort.setShouldFail(true);

        // when
        sut.processOutbox();

        // then
        OutboxEvent processed = outboxEventRepository.findAll().get(0);
        assertThat(processed.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(processed.getAttemptCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("attemptCount가 MAX에 도달한 이벤트는 FAILED로 전이한다")
    void processOutbox_최대_재시도_초과_FAILED() {
        // given — attemptCount=2, availableAt=과거인 이벤트 (다음 실패 시 3회 → FAILED)
        Notification notification = saveNotification(100L);
        saveDevice(100L, "token-abc");
        LocalDateTime past = LocalDateTime.now(fixedClock).minusMinutes(1);
        OutboxEvent event = OutboxEvent.reconstitute(
                null, notification.getId(), OutboxStatus.PENDING,
                "dedup:fail", 2, past, past);
        outboxEventRepository.save(event);
        pushNotificationPort.setShouldFail(true);

        // when
        sut.processOutbox();

        // then
        OutboxEvent processed = outboxEventRepository.findAll().get(0);
        assertThat(processed.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(processed.getAttemptCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Notification이 없으면 attemptCount 증가 후 재시도 대기한다")
    void processOutbox_Notification_없음_재시도() {
        // given
        saveOutboxEvent(999L);

        // when
        sut.processOutbox();

        // then
        OutboxEvent processed = outboxEventRepository.findAll().get(0);
        assertThat(processed.getAttemptCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("PENDING 이벤트가 없으면 아무 작업도 하지 않는다")
    void processOutbox_이벤트_없음() {
        // given — 없음

        // when
        sut.processOutbox();

        // then
        assertThat(pushNotificationPort.getSentMessages()).isEmpty();
    }

    // ── helpers ──

    private Notification saveNotification(Long userId) {
        Notification notification = Notification.create(
                userId, NotificationType.REQUEST_ARRIVED,
                ReferenceType.REQUEST, 1L,
                "title", "body", fixedClock);
        return notificationRepository.save(notification);
    }

    private void saveDevice(Long userId, String fcmToken) {
        UserDevice device = UserDevice.register(userId, DevicePlatform.ANDROID, fcmToken, fixedClock);
        userDeviceRepository.save(device);
    }

    private void saveOutboxEvent(Long notificationId) {
        OutboxEvent event = OutboxEvent.create(notificationId, "dedup:" + notificationId, fixedClock);
        outboxEventRepository.save(event);
    }
}
