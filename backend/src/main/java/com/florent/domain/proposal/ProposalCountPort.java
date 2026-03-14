package com.florent.domain.proposal;

import java.util.List;
import java.util.Map;

public interface ProposalCountPort {
    Map<Long, Map<ProposalStatus, Integer>> countByRequestIdsGroupByStatus(List<Long> requestIds);
}