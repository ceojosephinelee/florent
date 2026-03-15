package com.florent.domain.notification;

/**
 * 알림 저장 UseCase (Inbound Port).
 * 다른 도메인 Service가 알림 생성 시 이 인터페이스를 호출한다.
 * 기존 SaveNotificationPort와 동일한 계약을 유지한다.
 */
public interface SaveNotificationUseCase {
    void saveRequestArrived(Long sellerId, Long requestId);
    void saveProposalArrived(Long buyerId, Long proposalId);
    void saveReservationConfirmed(Long sellerId, Long reservationId);
}