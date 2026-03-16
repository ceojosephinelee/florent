package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.proposal.SaveProposalCommand;
import com.florent.domain.proposal.SaveProposalResult;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.SellerProposalListResult;
import com.florent.domain.proposal.StartProposalCommand;
import com.florent.domain.proposal.StartProposalResult;
import com.florent.domain.proposal.SubmitProposalCommand;
import com.florent.domain.proposal.SubmitProposalResult;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeProposalRepository;
import com.florent.fake.FakeSaveNotificationUseCase;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerProposalServiceTest {

    private FakeProposalRepository proposalRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeSaveNotificationUseCase notificationPort;
    private SellerProposalService sut;

    private static final Long SELLER_ID = 10L;
    private static final Long SHOP_ID = 100L;
    private static final Long BUYER_ID = 1L;
    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        proposalRepository = new FakeProposalRepository();
        requestRepository = new FakeCurationRequestRepository();
        shopRepository = new FakeFlowerShopRepository();
        notificationPort = new FakeSaveNotificationUseCase();
        sut = new SellerProposalService(
                proposalRepository, requestRepository, shopRepository,
                notificationPort, fixedClock);
    }

    private CurationRequest createRequest(Long buyerId) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                buyerId, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")
        );
        return requestRepository.save(CurationRequest.create(cmd, fixedClock));
    }

    private void setupShop(Long shopId, Long sellerId) {
        shopRepository.save(FlowerShop.reconstitute(
                shopId, sellerId, "테스트꽃집", null, "010-1234-5678", "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610")));
    }

    private SaveProposalCommand validSaveCommand(Long proposalId, Long sellerId) {
        return new SaveProposalCommand(
                proposalId, sellerId, "봄의 향기",
                List.of("PINK"), List.of("장미"), List.of("리본"),
                "없음", "물을 자주 주세요", "봄 느낌 꽃다발",
                List.of("https://img.com/1.jpg"), "PICKUP_30M", "14:00",
                new BigDecimal("35000"));
    }

    // ─── start ───

    @Test
    @DisplayName("start() — 정상 DRAFT 생성")
    void start_정상_DRAFT_생성() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);

        // when
        StartProposalResult result = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        // then
        assertThat(result.proposalId()).isNotNull();
        assertThat(result.status()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(result.expiresAt()).isNotNull();
    }

    @Test
    @DisplayName("start() — 존재하지 않는 요청 REQUEST_NOT_FOUND")
    void start_존재하지_않는_요청_REQUEST_NOT_FOUND() {
        // given
        setupShop(SHOP_ID, SELLER_ID);

        // when & then
        assertThatThrownBy(() -> sut.start(new StartProposalCommand(999L, SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("start() — OPEN이 아닌 요청 REQUEST_NOT_OPEN")
    void start_OPEN이_아닌_요청_REQUEST_NOT_OPEN() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        request.expire();
        requestRepository.save(request);
        setupShop(SHOP_ID, SELLER_ID);

        // when & then
        assertThatThrownBy(() -> sut.start(new StartProposalCommand(request.getId(), SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_OPEN);
    }

    @Test
    @DisplayName("start() — 중복 제안 PROPOSAL_ALREADY_EXISTS")
    void start_중복_제안_PROPOSAL_ALREADY_EXISTS() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        // when & then
        assertThatThrownBy(() -> sut.start(new StartProposalCommand(request.getId(), SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_ALREADY_EXISTS);
    }

    // ─── save ───

    @Test
    @DisplayName("save() — 정상 임시저장")
    void save_정상_임시저장() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        // when
        SaveProposalResult result = sut.save(validSaveCommand(started.proposalId(), SELLER_ID));

        // then
        assertThat(result.status()).isEqualTo(ProposalStatus.DRAFT);
        Proposal saved = proposalRepository.findById(started.proposalId()).orElseThrow();
        assertThat(saved.getConceptTitle()).isEqualTo("봄의 향기");
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("35000"));
    }

    @Test
    @DisplayName("save() — 존재하지 않는 제안 PROPOSAL_NOT_FOUND")
    void save_존재하지_않는_제안_PROPOSAL_NOT_FOUND() {
        // given
        setupShop(SHOP_ID, SELLER_ID);

        // when & then
        assertThatThrownBy(() -> sut.save(validSaveCommand(999L, SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_NOT_FOUND);
    }

    @Test
    @DisplayName("save() — 소유자 아닌 경우 FORBIDDEN")
    void save_소유자_아닌_경우_FORBIDDEN() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        Long otherSellerId = 20L;
        setupShop(200L, otherSellerId);

        // when & then
        assertThatThrownBy(() -> sut.save(validSaveCommand(started.proposalId(), otherSellerId)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("save() — DRAFT가 아닌 제안 PROPOSAL_NOT_EDITABLE")
    void save_DRAFT가_아닌_제안_PROPOSAL_NOT_EDITABLE() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));
        sut.save(validSaveCommand(started.proposalId(), SELLER_ID));
        sut.submit(new SubmitProposalCommand(started.proposalId(), SELLER_ID));

        // when & then
        assertThatThrownBy(() -> sut.save(validSaveCommand(started.proposalId(), SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_NOT_EDITABLE);
    }

    // ─── submit ───

    @Test
    @DisplayName("submit() — 정상 제출 및 알림 발송")
    void submit_정상_제출_알림_발송() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));
        sut.save(validSaveCommand(started.proposalId(), SELLER_ID));

        // when
        SubmitProposalResult result = sut.submit(
                new SubmitProposalCommand(started.proposalId(), SELLER_ID));

        // then
        assertThat(result.status()).isEqualTo(ProposalStatus.SUBMITTED);
        assertThat(result.submittedAt()).isNotNull();
        assertThat(notificationPort.getProposalRecords()).hasSize(1);
        assertThat(notificationPort.getProposalRecords().get(0).buyerId()).isEqualTo(BUYER_ID);
    }

    @Test
    @DisplayName("submit() — 소유자 아닌 경우 FORBIDDEN")
    void submit_소유자_아닌_경우_FORBIDDEN() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));
        sut.save(validSaveCommand(started.proposalId(), SELLER_ID));

        Long otherSellerId = 20L;
        setupShop(200L, otherSellerId);

        // when & then
        assertThatThrownBy(() -> sut.submit(
                new SubmitProposalCommand(started.proposalId(), otherSellerId)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("submit() — OPEN이 아닌 요청 REQUEST_NOT_OPEN")
    void submit_OPEN이_아닌_요청_REQUEST_NOT_OPEN() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));
        sut.save(validSaveCommand(started.proposalId(), SELLER_ID));

        // 요청을 EXPIRED로 변경
        request.expire();
        requestRepository.save(request);

        // when & then
        assertThatThrownBy(() -> sut.submit(
                new SubmitProposalCommand(started.proposalId(), SELLER_ID)))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_OPEN);
    }

    // ─── getMyProposals ───

    @Test
    @DisplayName("getMyProposals() — 정상 조회")
    void getMyProposals_정상_조회() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        // when
        SellerProposalListResult result = sut.getMyProposals(SELLER_ID, 0, 20);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("getMyProposals() — 제안 없으면 빈 목록")
    void getMyProposals_제안_없으면_빈_목록() {
        // given
        setupShop(SHOP_ID, SELLER_ID);

        // when
        SellerProposalListResult result = sut.getMyProposals(SELLER_ID, 0, 20);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    // ─── getSellerProposalDetail ───

    @Test
    @DisplayName("getSellerProposalDetail() — 정상 조회")
    void getSellerProposalDetail_정상_조회() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));
        sut.save(validSaveCommand(started.proposalId(), SELLER_ID));

        // when
        ProposalDetail detail = sut.getSellerProposalDetail(started.proposalId(), SELLER_ID);

        // then
        assertThat(detail.proposalId()).isEqualTo(started.proposalId());
        assertThat(detail.requestId()).isEqualTo(request.getId());
        assertThat(detail.status()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(detail.shopName()).isEqualTo("테스트꽃집");
        assertThat(detail.conceptTitle()).isEqualTo("봄의 향기");
        assertThat(detail.price()).isEqualByComparingTo(new BigDecimal("35000"));
    }

    @Test
    @DisplayName("getSellerProposalDetail() — 존재하지 않는 제안 PROPOSAL_NOT_FOUND")
    void getSellerProposalDetail_존재하지_않는_제안_PROPOSAL_NOT_FOUND() {
        // given
        setupShop(SHOP_ID, SELLER_ID);

        // when & then
        assertThatThrownBy(() -> sut.getSellerProposalDetail(999L, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_NOT_FOUND);
    }

    @Test
    @DisplayName("getSellerProposalDetail() — 소유자 아닌 경우 FORBIDDEN")
    void getSellerProposalDetail_소유자_아닌_경우_FORBIDDEN() {
        // given
        CurationRequest request = createRequest(BUYER_ID);
        setupShop(SHOP_ID, SELLER_ID);
        StartProposalResult started = sut.start(new StartProposalCommand(request.getId(), SELLER_ID));

        Long otherSellerId = 20L;
        setupShop(200L, otherSellerId);

        // when & then
        assertThatThrownBy(() -> sut.getSellerProposalDetail(started.proposalId(), otherSellerId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
