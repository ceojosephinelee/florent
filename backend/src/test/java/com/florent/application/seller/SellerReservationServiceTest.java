package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.reservation.ConfirmReservationCommand;
import com.florent.domain.reservation.ConfirmReservationResult;
import com.florent.domain.reservation.SellerReservationDetailResult;
import com.florent.domain.reservation.SellerReservationSummaryResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.application.buyer.BuyerReservationService;
import com.florent.fake.FakeBuyerRepository;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeProposalRepository;
import com.florent.fake.FakeReservationRepository;
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

class SellerReservationServiceTest {

    private FakeReservationRepository reservationRepository;
    private FakeProposalRepository proposalRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeBuyerRepository buyerRepository;
    private FakeSaveNotificationUseCase notificationUseCase;
    private BuyerReservationService buyerReservationService;
    private SellerReservationService sut;

    private static final Long BUYER_ID = 1L;
    private static final Long SELLER_ID = 10L;
    private static final Long SHOP_ID = 100L;
    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        shopRepository = new FakeFlowerShopRepository();
        buyerRepository = new FakeBuyerRepository();
        reservationRepository = new FakeReservationRepository(
                requestRepository, proposalRepository, shopRepository);

        notificationUseCase = new FakeSaveNotificationUseCase();

        buyerReservationService = new BuyerReservationService(
                reservationRepository, proposalRepository,
                requestRepository, shopRepository, fixedClock);

        sut = new SellerReservationService(
                reservationRepository, proposalRepository,
                requestRepository, buyerRepository, shopRepository,
                notificationUseCase, fixedClock);
    }

    private CurationRequest createOpenRequest(Long buyerId) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                buyerId, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                null);
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

    private void setupShopAndBuyer() {
        shopRepository.save(FlowerShop.reconstitute(
                SHOP_ID, SELLER_ID, "테스트꽃집", null, "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));
        buyerRepository.save(Buyer.reconstitute(BUYER_ID, 1L, "구매자닉네임"));
    }

    private Long createReservation() {
        CurationRequest request = createOpenRequest(BUYER_ID);
        Proposal proposal = createSubmittedProposal(request.getId(), SHOP_ID);
        return buyerReservationService.confirm(
                new ConfirmReservationCommand(BUYER_ID, proposal.getId(), "idem-key-1"))
                .reservationId();
    }

    // ─── confirmBySeller ───

    @Test
    @DisplayName("confirmBySeller() — PENDING_CONTACT → CONFIRMED 전이")
    void confirmBySeller_정상_확정() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();

        // when
        ConfirmReservationResult result = sut.confirmBySeller(reservationId, SELLER_ID);

        // then
        assertThat(result.reservationId()).isEqualTo(reservationId);
        assertThat(result.status()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("confirmBySeller() — 구매자에게 RESERVATION_CONFIRMED 알림이 생성된다")
    void confirmBySeller_알림_생성() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();

        // when
        sut.confirmBySeller(reservationId, SELLER_ID);

        // then
        assertThat(notificationUseCase.getReservationRecords()).hasSize(1);
        assertThat(notificationUseCase.getReservationRecords().get(0).buyerId())
                .isEqualTo(BUYER_ID);
    }

    @Test
    @DisplayName("confirmBySeller() — 이미 CONFIRMED 상태면 INVALID_RESERVATION_STATE")
    void confirmBySeller_이미_확정_INVALID_STATE() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();
        sut.confirmBySeller(reservationId, SELLER_ID);

        // when & then
        assertThatThrownBy(() -> sut.confirmBySeller(reservationId, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.INVALID_RESERVATION_STATE);
    }

    @Test
    @DisplayName("confirmBySeller() — 존재하지 않는 예약 RESERVATION_NOT_FOUND")
    void confirmBySeller_존재하지_않는_예약() {
        // given
        setupShopAndBuyer();

        // when & then
        assertThatThrownBy(() -> sut.confirmBySeller(999L, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("confirmBySeller() — 다른 판매자 접근 시 FORBIDDEN")
    void confirmBySeller_타인_예약_FORBIDDEN() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();

        Long otherSellerId = 20L;
        shopRepository.save(FlowerShop.reconstitute(
                200L, otherSellerId, "다른꽃집", null, null, "서울시 마포구",
                new BigDecimal("37.55"), new BigDecimal("126.92")));

        // when & then
        assertThatThrownBy(() -> sut.confirmBySeller(reservationId, otherSellerId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }

    // ─── getList ───

    @Test
    @DisplayName("getList() — 판매자의 예약 목록이 반환된다")
    void getList_정상_조회() {
        // given
        setupShopAndBuyer();
        createReservation();

        // when
        List<SellerReservationSummaryResult> results = sut.getList(SELLER_ID);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).conceptTitle()).isEqualTo("봄의 향기");
        assertThat(results.get(0).price()).isEqualByComparingTo(new BigDecimal("35000"));
        assertThat(results.get(0).buyerNickName()).isEqualTo("구매자닉네임");
        assertThat(results.get(0).status()).isEqualTo("PENDING_CONTACT");
    }

    @Test
    @DisplayName("getList() — 예약이 없으면 빈 목록 반환")
    void getList_빈_목록() {
        // given
        setupShopAndBuyer();

        // when
        List<SellerReservationSummaryResult> results = sut.getList(SELLER_ID);

        // then
        assertThat(results).isEmpty();
    }

    // ─── getDetail ───

    @Test
    @DisplayName("getDetail() — 예약 상세가 반환된다")
    void getDetail_정상_조회() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();

        // when
        SellerReservationDetailResult result = sut.getDetail(reservationId, SELLER_ID);

        // then
        assertThat(result.reservationId()).isEqualTo(reservationId);
        assertThat(result.status()).isEqualTo("PENDING_CONTACT");
        assertThat(result.buyerNickName()).isEqualTo("구매자닉네임");
        assertThat(result.proposal().conceptTitle()).isEqualTo("봄의 향기");
        assertThat(result.request().budgetTier()).isEqualTo("TIER2");
        assertThat(result.placeAddressText()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("getDetail() — 존재하지 않는 예약 RESERVATION_NOT_FOUND")
    void getDetail_존재하지_않는_예약_RESERVATION_NOT_FOUND() {
        // given
        setupShopAndBuyer();

        // when & then
        assertThatThrownBy(() -> sut.getDetail(999L, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.RESERVATION_NOT_FOUND);
    }

    @Test
    @DisplayName("getDetail() — 다른 판매자의 예약 조회 시 FORBIDDEN")
    void getDetail_타인_예약_FORBIDDEN() {
        // given
        setupShopAndBuyer();
        Long reservationId = createReservation();

        Long otherSellerId = 20L;
        shopRepository.save(FlowerShop.reconstitute(
                200L, otherSellerId, "다른꽃집", null, null, "서울시 마포구",
                new BigDecimal("37.55"), new BigDecimal("126.92")));

        // when & then
        assertThatThrownBy(() -> sut.getDetail(reservationId, otherSellerId))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
