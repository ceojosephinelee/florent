package com.florent.fake;

import com.florent.domain.proposal.ProposalCountPort;
import com.florent.domain.proposal.ProposalStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeProposalCountPort implements ProposalCountPort {

    private final Map<Long, Map<ProposalStatus, Integer>> store = new HashMap<>();

    @Override
    public Map<Long, Map<ProposalStatus, Integer>> countByRequestIdsGroupByStatus(List<Long> requestIds) {
        Map<Long, Map<ProposalStatus, Integer>> result = new HashMap<>();
        for (Long requestId : requestIds) {
            if (store.containsKey(requestId)) {
                result.put(requestId, store.get(requestId));
            }
        }
        return result;
    }

    public void setCount(Long requestId, ProposalStatus status, int count) {
        store.computeIfAbsent(requestId, k -> new EnumMap<>(ProposalStatus.class))
                .put(status, count);
    }
}
