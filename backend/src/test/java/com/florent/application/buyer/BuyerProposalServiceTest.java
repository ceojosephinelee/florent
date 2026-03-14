package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.proposal.ProposalSummary;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyerProposalServiceTest {

    private FakeProposalRepository proposalRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private BuyerProposalService sut;

    private static final Long BUYER_ID = 1L;
    private static final Long SHOP_ID = 100L;
    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-03-15T10:00:00Z"), ZoneId.of("Asia/Seoul"));

    @BeforeEach
    void setUp() {
        proposalRepository = new FakeProposalRepository();
        requestRepository = new FakeCurationRequestRepository();
        shopRepository = new FakeFlowerShopRepository();
        sut = new BuyerProposalService(proposalRepository, requestRepository, shopRepository);
    }

    private CurationRequest createRequest() {
        CreateRequestCommand cmd = new CreateRequestCommand(
                BUYER_ID, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")
        );
        return requestRepository.save(CurationRequest.create(cmd, fixedClock));
    }

    private Proposal createAndSaveProposal(Long requestId, Long shopId, ProposalStatus targetStatus) {
        Proposal proposal = Proposal.create(requestId, shopId, fixedClock);
        if (targetStatus == ProposalStatus.SUBMITTED) {
            proposal.submit(fixedClock);
        } else if (targetStatus == ProposalStatus.EXPIRED) {
            proposal.expire();
        }
        return proposalRepository.save(proposal);
    }

    private void setupShop(Long shopId, String name) {
        shopRepository.save(FlowerShop.reconstitute(
                shopId, 10L, name, "010-1234-5678", "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610")));
    }

    // ─── getProposalsByRequestId ───

    @Test
    @DisplayName("정상 조회 — SUBMITTED, EXPIRED만 반환된다")
    void getProposalsByRequestId_정상_조회() {
        // given
        CurationRequest request = createRequest();
        setupShop(SHOP_ID, "꽃집A");
        setupShop(SHOP_ID + 1, "꽃집B");
        setupShop(SHOP_ID + 2, "꽃집C");

        createAndSaveProposal(request.getId(), SHOP_ID, ProposalStatus.SUBMITTED);
        createAndSaveProposal(request.getId(), SHOP_ID + 1, ProposalStatus.EXPIRED);
        createAndSaveProposal(request.getId(), SHOP_ID + 2, ProposalStatus.DRAFT);

        // when
        List<ProposalSummary> result = sut.getProposalsByRequestId(request.getId(), BUYER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ProposalSummary::status)
                .containsExactlyInAnyOrder(ProposalStatus.SUBMITTED, ProposalStatus.EXPIRED);
    }

    @Test
    @DisplayName("DRAFT 제안은 필터링되어 제외된다")
    void getProposalsByRequestId_DRAFT_필터링() {
        // given
        CurationRequest request = createRequest();
        setupShop(SHOP_ID, "꽃집A");
        createAndSaveProposal(request.getId(), SHOP_ID, ProposalStatus.DRAFT);

        // when
        List<ProposalSummary> result = sut.getProposalsByRequestId(request.getId(), BUYER_ID);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("소유자가 아닐 때 FORBIDDEN 예외")
    void getProposalsByRequestId_소유자_아닐_때_FORBIDDEN() {
        // given
        CurationRequest request = createRequest();

        // when & then
        assertThatThrownBy(() -> sut.getProposalsByRequestId(request.getId(), 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    // ─── getProposalDetail ───

    @Test
    @DisplayName("정상 상세 조회 — 모든 필드가 포함된다")
    void getProposalDetail_정상_조회() {
        // given
        CurationRequest request = createRequest();
        setupShop(SHOP_ID, "꽃집A");
        Proposal saved = createAndSaveProposal(request.getId(), SHOP_ID, ProposalStatus.SUBMITTED);

        // when
        ProposalDetail result = sut.getProposalDetail(saved.getId(), BUYER_ID);

        // then
        assertThat(result.proposalId()).isEqualTo(saved.getId());
        assertThat(result.shopName()).isEqualTo("꽃집A");
        assertThat(result.shopPhone()).isEqualTo("010-1234-5678");
        assertThat(result.status()).isEqualTo(ProposalStatus.SUBMITTED);
    }

    @Test
    @DisplayName("존재하지 않는 proposal → PROPOSAL_NOT_FOUND")
    void getProposalDetail_존재하지_않는_proposal_PROPOSAL_NOT_FOUND() {
        // when & then
        assertThatThrownBy(() -> sut.getProposalDetail(999L, BUYER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_NOT_FOUND);
    }
}
