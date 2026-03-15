package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.reservation.BuyerReservationDetailResult;
import com.florent.domain.reservation.BuyerReservationSummaryResult;
import com.florent.domain.reservation.ConfirmReservationCommand;
import com.florent.domain.reservation.ConfirmReservationResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakePaymentPort;
import com.florent.fake.FakePaymentRepository;
import com.florent.fake.FakeProposalRepository;
import com.florent.fake.FakeReservationRepository;
import com.florent.fake.FakeSaveNotificationPort;
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

class BuyerReservationServiceTest {

    private FakeReservationRepository reservationRepository;
    private FakePaymentRepository paymentRepository;
    private FakeProposalRepository proposalRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeSaveNotificationPort notificationPort;
    private BuyerReservationService sut;

    private static final Long BUYER_ID = 1L;
    private static final Long SELLER_ID = 10L;
    private static final Long SHOP_ID = 100L;
    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        shopRepository = new FakeFlowerShopRepository();
        paymentRepository = new FakePaymentRepository();
        reservationRepository = new FakeReservationRepository(
                requestRepository, proposalRepository, shopRepository);
        notificationPort = new FakeSaveNotificationPort();
        sut = new BuyerReservationService(
                reservationRepository, paymentRepository, new FakePaymentPort(),
                proposalRepository, requestRepository,
                shopRepository, notificationPort, fixedClock);
    }

    private CurationRequest createOpenRequest(Long buyerId) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                buyerId, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));
        return requestRepository.save(CurationRequest.create(cmd, fixedClock));
    }

    private Proposal createSubmittedProposal(Long requestId, Long shopId) {
        Proposal proposal = Proposal.create(requestId, shopId, fixedClock);
        proposal.updateDraft("봄의 향기", List.of("PINK"), List.of("장미"),
                List.of("리본"), null, null, "봄 느낌 꽃다발",
                List.of("https://img.com/1.jpg"), "PICKUP_30M", "14:00",
                new BigDecimal("35000"));
        proposal.submit(fixedClock);
        return proposalRepository.save(proposal);
    }

    private void setupShop() {
        shopRepository.save(FlowerShop.reconstitute(
                SHOP_ID, SELLER_ID, "테스트꽃집", "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));
    }

    // ─── confirm ───

    @Test
    @DisplayName("confirm() — 정상적으로 예약이 확정되고 결제가 생성된다")
    void confirm_정상_예약_확정() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);

        // when
        ConfirmReservationResult result = sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // then
        assertThat(result.reservationId()).isNotNull();
        assertThat(result.status()).isEqualTo("CONFIRMED");
        assertThat(result.paymentStatus()).isEqualTo("SUCCEEDED");
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("35000"));
    }

    @Test
    @DisplayName("confirm() — 요청이 CONFIRMED 상태로 전이한다")
    void confirm_요청_CONFIRMED_전이() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);

        // when
        sut.confirm(new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // then
        CurationRequest updated = requestRepository.findById(request.getId()).orElseThrow();
        assertThat(updated.getStatus().name()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("confirm() — 선택된 제안은 SELECTED, 나머지는 NOT_SELECTED로 전이한다")
    void confirm_제안_상태_전이() {
        // given
        setupShop();
        Long otherShopId = 200L;
        shopRepository.save(FlowerShop.reconstitute(
                otherShopId, 20L, "다른꽃집", null, "서울시 마포구",
                new BigDecimal("37.55"), new BigDecimal("126.92")));

        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal selectedProposal = createSubmittedProposal(request.getId(), SHOP_ID);
        Proposal otherProposal = createSubmittedProposal(request.getId(), otherShopId);

        // when
        sut.confirm(new ConfirmReservationCommand(BUYER_ID, selectedProposal.getId(), "idem-key-1"));

        // then
        Proposal selected = proposalRepository.findById(selectedProposal.getId()).orElseThrow();
        Proposal notSelected = proposalRepository.findById(otherProposal.getId()).orElseThrow();
        assertThat(selected.getStatus()).isEqualTo(ProposalStatus.SELECTED);
        assertThat(notSelected.getStatus()).isEqualTo(ProposalStatus.NOT_SELECTED);
    }

    @Test
    @DisplayName("confirm() — 선택된 판매자에게 알림이 발송된다")
    void confirm_알림_발송() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);

        // when
        sut.confirm(new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // then
        assertThat(notificationPort.getReservationRecords()).hasSize(1);
        assertThat(notificationPort.getReservationRecords().get(0).sellerId()).isEqualTo(SELLER_ID);
    }

    @Test
    @DisplayName("confirm() — 중복 idempotencyKey로 DUPLICATE_PAYMENT 예외")
    void confirm_중복_결제_DUPLICATE_PAYMENT() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);
        sut.confirm(new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // 새 요청/제안으로 같은 idempotencyKey 사용
        CurationRequest request2 = createOpenRequest(BUYER_ID);
        Proposal proposal2 = createSubmittedProposal(request2.getId(), SHOP_ID);

        // when & then
        assertThatThrownBy(() -> sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal2.getId(), "idem-key-1")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.DUPLICATE_PAYMENT);
    }

    @Test
    @DisplayName("confirm() — 존재하지 않는 제안 PROPOSAL_NOT_FOUND")
    void confirm_존재하지_않는_제안_PROPOSAL_NOT_FOUND() {
        // when & then
        assertThatThrownBy(() -> sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, 999L, "idem-key-1")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.PROPOSAL_NOT_FOUND);
    }

    @Test
    @DisplayName("confirm() — 다른 구매자의 요청에 대한 제안 선택 시 FORBIDDEN")
    void confirm_타인_요청_FORBIDDEN() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);

        // when & then — 다른 buyerId로 시도
        assertThatThrownBy(() -> sut.confirm(
                new ConfirmReservationCommand(999L, proposal.getId(), "idem-key-1")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    @Test
    @DisplayName("confirm() — 이미 확정된 요청 REQUEST_ALREADY_CONFIRMED")
    void confirm_이미_확정된_요청_REQUEST_ALREADY_CONFIRMED() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal1 = createSubmittedProposal(request.getId(), SHOP_ID);

        Long otherShopId = 200L;
        shopRepository.save(FlowerShop.reconstitute(
                otherShopId, 20L, "다른꽃집", null, "서울시 마포구",
                new BigDecimal("37.55"), new BigDecimal("126.92")));
        Proposal proposal2 = createSubmittedProposal(request.getId(), otherShopId);

        sut.confirm(new ConfirmReservationCommand(BUYER_ID, proposal1.getId(), "idem-key-1"));

        // when & then
        assertThatThrownBy(() -> sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal2.getId(), "idem-key-2")))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_ALREADY_CONFIRMED);
    }

    // ─── getList ───

    @Test
    @DisplayName("getList() — 구매자의 예약 목록이 반환된다")
    void getList_정상_조회() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);
        sut.confirm(new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // when
        List<BuyerReservationSummaryResult> results = sut.getList(BUYER_ID);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).shopName()).isEqualTo("테스트꽃집");
        assertThat(results.get(0).conceptTitle()).isEqualTo("봄의 향기");
        assertThat(results.get(0).price()).isEqualByComparingTo(new BigDecimal("35000"));
        assertThat(results.get(0).status()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("getList() — 예약이 없으면 빈 목록 반환")
    void getList_빈_목록() {
        // when
        List<BuyerReservationSummaryResult> results = sut.getList(BUYER_ID);

        // then
        assertThat(results).isEmpty();
    }

    // ─── getDetail ───

    @Test
    @DisplayName("getDetail() — 예약 상세가 반환된다")
    void getDetail_정상_조회() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);
        ConfirmReservationResult confirmResult = sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // when
        BuyerReservationDetailResult result = sut.getDetail(
                confirmResult.reservationId(), BUYER_ID);

        // then
        assertThat(result.reservationId()).isEqualTo(confirmResult.reservationId());
        assertThat(result.status()).isEqualTo("CONFIRMED");
        assertThat(result.proposal().conceptTitle()).isEqualTo("봄의 향기");
        assertThat(result.shop().name()).isEqualTo("테스트꽃집");
        assertThat(result.request().requestId()).isEqualTo(request.getId());
        assertThat(result.request().budgetTier()).isEqualTo("TIER2");
    }

    @Test
    @DisplayName("getDetail() — 존재하지 않는 예약 RESERVATION_NOT_FOUND")
    void getDetail_존재하지_않는_예약_RESERVATION_NOT_FOUND() {
        // when & then
        assertThatThrownBy(() -> sut.getDetail(999L, BUYER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("getDetail() — 타인의 예약 조회 시 FORBIDDEN")
    void getDetail_타인_예약_FORBIDDEN() {
        // given
        setupShop();
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);
        ConfirmReservationResult confirmResult = sut.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"));

        // when & then
        assertThatThrownBy(() -> sut.getDetail(confirmResult.reservationId(), 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
