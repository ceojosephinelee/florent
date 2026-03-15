package com.florent.adapter.out.notification;

import com.florent.domain.notification.SaveNotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NoOpSaveNotificationAdapter implements SaveNotificationPort {

    @Override
    public void saveRequestArrived(Long sellerId, Long requestId) {
        log.info("[Mock] REQUEST_ARRIVED 알림 — sellerId={}, requestId={}", sellerId, requestId);
    }

    @Override
    public void saveProposalArrived(Long buyerId, Long proposalId) {
        log.info("[Mock] PROPOSAL_ARRIVED 알림 — buyerId={}, proposalId={}", buyerId, proposalId);
    }

    @Override
    public void saveReservationConfirmed(Long sellerId, Long reservationId) {
        log.info("[Mock] RESERVATION_CONFIRMED 알림 — sellerId={}, reservationId={}", sellerId, reservationId);
    }
}