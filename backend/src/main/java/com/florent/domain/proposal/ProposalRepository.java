package com.florent.domain.proposal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProposalRepository {
    Proposal save(Proposal proposal);
    Optional<Proposal> findById(Long id);
    List<Proposal> findAllByIds(List<Long> ids);
    List<Proposal> findByRequestId(Long requestId);
    ProposalPage findByFlowerShopId(Long flowerShopId, int page, int size);
    boolean existsByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId);
    Optional<Proposal> findByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId);
    List<Proposal> findByRequestIdsAndFlowerShopId(List<Long> requestIds, Long flowerShopId);
    List<Proposal> findAllByFlowerShopId(Long flowerShopId);
    List<Proposal> findExpirableBefore(LocalDateTime now);
}
