package com.florent.adapter.out.persistence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, Long> {

    boolean existsByRequestId(Long requestId);

    @Query("SELECT r FROM ReservationJpaEntity r "
            + "JOIN CurationRequestJpaEntity cr ON r.requestId = cr.id "
            + "WHERE cr.buyerId = :buyerId "
            + "ORDER BY r.confirmedAt DESC")
    List<ReservationJpaEntity> findAllByBuyerId(@Param("buyerId") Long buyerId);

    @Query("SELECT r FROM ReservationJpaEntity r "
            + "JOIN ProposalJpaEntity p ON r.proposalId = p.id "
            + "JOIN FlowerShopJpaEntity fs ON p.flowerShopId = fs.id "
            + "WHERE fs.sellerId = :sellerId "
            + "ORDER BY r.confirmedAt DESC")
    List<ReservationJpaEntity> findAllBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT r.id FROM ReservationJpaEntity r WHERE r.requestId IN :requestIds")
    List<Long> findIdsByRequestIds(@Param("requestIds") List<Long> requestIds);

    @Query("SELECT r.id FROM ReservationJpaEntity r WHERE r.proposalId IN :proposalIds")
    List<Long> findIdsByProposalIds(@Param("proposalIds") List<Long> proposalIds);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM ReservationJpaEntity r WHERE r.requestId IN :requestIds")
    void deleteByRequestIds(@Param("requestIds") List<Long> requestIds);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM ReservationJpaEntity r WHERE r.proposalId IN :proposalIds")
    void deleteByProposalIds(@Param("proposalIds") List<Long> proposalIds);
}
