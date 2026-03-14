package com.florent.domain.notification;

public interface SaveNotificationPort {
    void saveRequestArrived(Long sellerId, Long requestId);
    void saveProposalArrived(Long buyerId, Long proposalId);
}