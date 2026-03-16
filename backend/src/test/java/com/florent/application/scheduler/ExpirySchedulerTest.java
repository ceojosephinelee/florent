package com.florent.application.scheduler;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeProposalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExpirySchedulerTest {

    private FakeCurationRequestRepository requestRepository;
    private FakeProposalRepository proposalRepository;
    private ExpiryScheduler sut;

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final Instant BASE_TIME = Instant.parse("2026-03-15T10:00:00Z");
    private final Clock pastClock = Clock.fixed(BASE_TIME, ZONE);
    private final Clock futureClock = Clock.fixed(BASE_TIME.plusSeconds(49 * 3600), ZONE);

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        sut = new ExpiryScheduler(requestRepository, proposalRepository, futureClock);
    }

    // ── Request Expiry ──

    @Test
    @DisplayName("48시간 경과한 OPEN 요청을 EXPIRED로 전이한다")
    void expireRequests_48시간_경과_만료() {
        // given
        CurationRequest request = createRequest(pastClock);
        requestRepository.save(request);

        // when
        sut.expireRequests();

        // then
        CurationRequest found = requestRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(RequestStatus.EXPIRED);
    }

    @Test
    @DisplayName("48시간 미경과 OPEN 요청은 만료시키지 않는다")
    void expireRequests_미경과_유지() {
        // given
        Clock recentClock = Clock.fixed(BASE_TIME.plusSeconds(47 * 3600), ZONE);
        sut = new ExpiryScheduler(requestRepository, proposalRepository, recentClock);
        CurationRequest request = createRequest(pastClock);
        requestRepository.save(request);

        // when
        sut.expireRequests();

        // then
        CurationRequest found = requestRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(RequestStatus.OPEN);
    }

    @Test
    @DisplayName("CONFIRMED 요청은 만료 대상에서 제외된다")
    void expireRequests_CONFIRMED_제외() {
        // given
        CurationRequest request = createRequest(pastClock);
        request.confirm();
        requestRepository.save(request);

        // when
        sut.expireRequests();

        // then
        CurationRequest found = requestRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }

    // ── Proposal Expiry ──

    @Test
    @DisplayName("24시간 경과한 DRAFT 제안을 EXPIRED로 전이한다")
    void expireProposals_DRAFT_만료() {
        // given
        Proposal proposal = Proposal.create(1L, 1L, pastClock);
        proposalRepository.save(proposal);

        // when
        sut.expireProposals();

        // then
        Proposal found = proposalRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(ProposalStatus.EXPIRED);
    }

    @Test
    @DisplayName("24시간 경과한 SUBMITTED 제안을 EXPIRED로 전이한다")
    void expireProposals_SUBMITTED_만료() {
        // given
        Proposal proposal = createSubmittedProposal(pastClock);
        proposalRepository.save(proposal);

        // when
        sut.expireProposals();

        // then
        Proposal found = proposalRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(ProposalStatus.EXPIRED);
    }

    @Test
    @DisplayName("SELECTED 제안은 만료 대상에서 제외된다")
    void expireProposals_SELECTED_제외() {
        // given
        Proposal proposal = createSubmittedProposal(pastClock);
        proposal.select();
        proposalRepository.save(proposal);

        // when
        sut.expireProposals();

        // then
        Proposal found = proposalRepository.findById(1L).orElseThrow();
        assertThat(found.getStatus()).isEqualTo(ProposalStatus.SELECTED);
    }

    // ── helpers ──

    private CurationRequest createRequest(Clock clock) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                1L,
                List.of("생일"),
                List.of("친구"),
                List.of("화사한"),
                BudgetTier.TIER2,
                FulfillmentType.PICKUP,
                LocalDate.of(2026, 3, 17),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구",
                new BigDecimal("37.123456"),
                new BigDecimal("127.123456"));
        return CurationRequest.create(cmd, clock);
    }

    private Proposal createSubmittedProposal(Clock clock) {
        Proposal proposal = Proposal.create(1L, 1L, clock);
        proposal.updateDraft("봄꽃 다발", List.of("핑크"), List.of("장미"),
                List.of("기본"), null, null, "예쁜 꽃다발",
                null, "PICKUP_30M", "14:00", new BigDecimal("30000"));
        proposal.submit(clock);
        return proposal;
    }
}
