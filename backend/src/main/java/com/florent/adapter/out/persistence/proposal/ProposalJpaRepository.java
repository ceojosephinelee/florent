package com.florent.adapter.out.persistence.proposal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProposalJpaRepository extends JpaRepository<ProposalJpaEntity, Long> {

    List<ProposalJpaEntity> findByRequestId(Long requestId);

    @Query("SELECT p.requestId, p.status, COUNT(p) "
            + "FROM ProposalJpaEntity p "
            + "WHERE p.requestId IN :requestIds "
            + "GROUP BY p.requestId, p.status")
    List<Object[]> countGroupByRequestIdAndStatus(@Param("requestIds") List<Long> requestIds);
}
