package com.florent.fake;

import com.florent.domain.notification.SaveNotificationPort;

import java.util.ArrayList;
import java.util.List;

public class FakeSaveNotificationPort implements SaveNotificationPort {

    private final List<NotificationRecord> records = new ArrayList<>();
    private final List<ProposalNotificationRecord> proposalRecords = new ArrayList<>();
    private final List<ReservationNotificationRecord> reservationRecords = new ArrayList<>();

    @Override
    public void saveRequestArrived(Long sellerId, Long requestId) {
        records.add(new NotificationRecord(sellerId, requestId));
    }

    @Override
    public void saveProposalArrived(Long buyerId, Long proposalId) {
        proposalRecords.add(new ProposalNotificationRecord(buyerId, proposalId));
    }

    @Override
    public void saveReservationConfirmed(Long sellerId, Long reservationId) {
        reservationRecords.add(new ReservationNotificationRecord(sellerId, reservationId));
    }

    public List<NotificationRecord> getRecords() {
        return List.copyOf(records);
    }

    public List<ProposalNotificationRecord> getProposalRecords() {
        return List.copyOf(proposalRecords);
    }

    public List<ReservationNotificationRecord> getReservationRecords() {
        return List.copyOf(reservationRecords);
    }

    public void clear() {
        records.clear();
        proposalRecords.clear();
        reservationRecords.clear();
    }

    public record NotificationRecord(Long sellerId, Long requestId) {}
    public record ProposalNotificationRecord(Long buyerId, Long proposalId) {}
    public record ReservationNotificationRecord(Long sellerId, Long reservationId) {}
}