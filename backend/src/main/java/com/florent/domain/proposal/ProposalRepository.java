package com.florent.domain.proposal;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository {
    Proposal save(Proposal proposal);
    Optional<Proposal> findById(Long id);
    List<Proposal> findAllByIds(List<Long> ids);
    List<Proposal> findByRequestId(Long requestId);
    ProposalPage findByFlowerShopId(Long flowerShopId, int page, int size);
    boolean existsByRequestIdAndFlowerShopId(Long requestId, Long flowerShopId);
}
