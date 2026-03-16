package com.florent.application.scheduler;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiryScheduler {

    private final CurationRequestRepository requestRepository;
    private final ProposalRepository proposalRepository;
    private final Clock clock;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireRequests() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<CurationRequest> expired = requestRepository.findOpenExpiredBefore(now);

        for (CurationRequest request : expired) {
            request.expire();
            requestRepository.save(request);
        }

        if (!expired.isEmpty()) {
            log.info("Expired {} requests", expired.size());
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void expireProposals() {
        LocalDateTime now = LocalDateTime.now(clock);
        List<Proposal> expirable = proposalRepository.findExpirableBefore(now);

        for (Proposal proposal : expirable) {
            proposal.expire();
            proposalRepository.save(proposal);
        }

        if (!expirable.isEmpty()) {
            log.info("Expired {} proposals", expirable.size());
        }
    }
}
