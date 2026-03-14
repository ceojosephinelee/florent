package com.florent.domain.proposal;

import java.util.List;

public interface GetProposalListUseCase {
    List<ProposalSummary> getProposalsByRequestId(Long requestId, Long buyerId);
}
