package com.florent.adapter.out.persistence.proposal;

import com.florent.domain.proposal.ProposalCountPort;
import com.florent.domain.proposal.ProposalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProposalCountAdapter implements ProposalCountPort {

    private final ProposalJpaRepository proposalJpaRepository;

    @Override
    public Map<Long, Map<ProposalStatus, Integer>> countByRequestIdsGroupByStatus(List<Long> requestIds) {
        if (requestIds.isEmpty()) {
            return Map.of();
        }

        List<Object[]> rows = proposalJpaRepository.countGroupByRequestIdAndStatus(requestIds);
        Map<Long, Map<ProposalStatus, Integer>> result = new HashMap<>();

        for (Object[] row : rows) {
            Long requestId = (Long) row[0];
            String statusStr = (String) row[1];
            int count = ((Number) row[2]).intValue();

            result.computeIfAbsent(requestId, k -> new EnumMap<>(ProposalStatus.class))
                    .put(ProposalStatus.valueOf(statusStr), count);
        }

        return result;
    }
}
