package com.florent.domain.notification;

/**
 * 알림 유형별 제목/본문 템플릿.
 * 순수 Java — 외부 의존 없음.
 */
public final class NotificationMessages {

    private NotificationMessages() {}

    public static String title(NotificationType type) {
        return switch (type) {
            case REQUEST_ARRIVED -> "새로운 큐레이션 요청이 도착했어요";
            case PROPOSAL_ARRIVED -> "새로운 제안서가 도착했어요";
            case RESERVATION_CONFIRMED -> "예약이 확정되었어요";
        };
    }

    public static String body(NotificationType type) {
        return switch (type) {
            case REQUEST_ARRIVED -> "내 가게 근처에 새로운 꽃 큐레이션 요청이 있어요. 확인해보세요!";
            case PROPOSAL_ARRIVED -> "플로리스트가 제안서를 보냈어요. 확인해보세요!";
            case RESERVATION_CONFIRMED -> "축하해요! 예약이 확정되었어요.";
        };
    }
}