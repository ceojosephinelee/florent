package com.florent.domain.proposal;

import java.util.List;
import java.util.Optional;

public interface ProposalRepository {
    Proposal save(Proposal proposal);
    Optional<Proposal> findById(Long id);
    List<Proposal> findByRequestId(Long requestId);
}
