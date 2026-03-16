package com.florent.adapter.out.persistence.proposal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProposalJpaRepository extends JpaRepository<ProposalJpaEntity, Long> {

    List<ProposalJpaEntity> findByRequestId(Long requestId);

    @Query("SELECT p.requestId, p.status, COUNT(p) "
            + "FROM ProposalJpaEntity p "
            + "WHERE p.requestId IN :requestIds "
            + "GROUP BY p.requestId, p.status")
    List<Object[]> countGroupByRequestIdAndStatus(@Param("requestIds") List<Long> requestIds);

    Page<ProposalJpaEntity> findByFlowerShopIdOrderByCreatedAtDesc(
            Long flowerShopId, Pageable pageable);

    boolean existsByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId);

    Optional<ProposalJpaEntity> findByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId);

    List<ProposalJpaEntity> findByRequestIdInAndFlowerShopId(List<Long> requestIds, Long flowerShopId);

    List<ProposalJpaEntity> findByFlowerShopId(Long flowerShopId);

    @Query("SELECT p FROM ProposalJpaEntity p "
            + "WHERE p.status IN ('DRAFT', 'SUBMITTED') AND p.expiresAt < :now")
    List<ProposalJpaEntity> findExpirableBefore(@Param("now") LocalDateTime now);
}
