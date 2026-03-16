package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SellerRequestDetailResult;
import com.florent.domain.request.SellerRequestListResult;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeProposalRepository;
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

class SellerRequestServiceTest {

    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeProposalRepository proposalRepository;
    private SellerRequestService sut;

    private static final Long SELLER_ID = 10L;
    private static final Clock FIXED_CLOCK = TestFixtures.FIXED_CLOCK;

    // 강남역 부근 좌표
    private static final BigDecimal SHOP_LAT = new BigDecimal("37.498095");
    private static final BigDecimal SHOP_LNG = new BigDecimal("127.027610");

    // 반경 2km 이내 좌표 (~500m 거리)
    private static final BigDecimal NEAR_LAT = new BigDecimal("37.500000");
    private static final BigDecimal NEAR_LNG = new BigDecimal("127.028000");

    // 반경 2km 밖 좌표 (~10km 거리)
    private static final BigDecimal FAR_LAT = new BigDecimal("37.570000");
    private static final BigDecimal FAR_LNG = new BigDecimal("127.000000");

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        shopRepository = new FakeFlowerShopRepository();
        proposalRepository = new FakeProposalRepository();
        sut = new SellerRequestService(requestRepository, shopRepository, proposalRepository);
    }

    private FlowerShop createShop() {
        FlowerShop shop = FlowerShop.create(
                SELLER_ID, "테스트 꽃집", null, null,
                "서울시 강남구", SHOP_LAT, SHOP_LNG);
        return shopRepository.save(shop);
    }

    private CurationRequest createRequest(BigDecimal lat, BigDecimal lng) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                1L,
                List.of("생일"),
                List.of("연인"),
                List.of("따뜻한"),
                BudgetTier.TIER2,
                FulfillmentType.PICKUP,
                LocalDate.of(2026, 3, 20),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구",
                lat, lng);
        CurationRequest request = CurationRequest.create(cmd, FIXED_CLOCK);
        return requestRepository.save(request);
    }

    // ─── getSellerRequests ───

    @Test
    @DisplayName("getSellerRequests() — 반경 2km 내 요청만 반환")
    void 반경_2km_내_요청만_반환() {
        // given
        createShop();
        createRequest(NEAR_LAT, NEAR_LNG);
        createRequest(FAR_LAT, FAR_LNG);

        // when
        SellerRequestListResult result = sut.getSellerRequests(SELLER_ID, 0, 20);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("getSellerRequests() — myProposalStatus가 올바르게 반환됨")
    void myProposalStatus_포함() {
        // given
        FlowerShop shop = createShop();
        CurationRequest request = createRequest(NEAR_LAT, NEAR_LNG);

        Proposal proposal = Proposal.create(request.getId(), shop.getId(), FIXED_CLOCK);
        proposalRepository.save(proposal);

        // when
        SellerRequestListResult result = sut.getSellerRequests(SELLER_ID, 0, 20);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).myProposalStatus()).isEqualTo("DRAFT");
    }

    @Test
    @DisplayName("getSellerRequests() — 제안 없으면 myProposalStatus null")
    void myProposalStatus_null() {
        // given
        createShop();
        createRequest(NEAR_LAT, NEAR_LNG);

        // when
        SellerRequestListResult result = sut.getSellerRequests(SELLER_ID, 0, 20);

        // then
        assertThat(result.content().get(0).myProposalStatus()).isNull();
    }

    @Test
    @DisplayName("getSellerRequests() — 꽃집 미등록 시 SHOP_NOT_FOUND")
    void 꽃집_미등록_예외() {
        // when & then
        assertThatThrownBy(() -> sut.getSellerRequests(SELLER_ID, 0, 20))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SHOP_NOT_FOUND);
    }

    @Test
    @DisplayName("getSellerRequests() — 페이지네이션 동작")
    void 페이지네이션() {
        // given
        createShop();
        for (int i = 0; i < 5; i++) {
            createRequest(NEAR_LAT, NEAR_LNG);
        }

        // when
        SellerRequestListResult page1 = sut.getSellerRequests(SELLER_ID, 0, 2);
        SellerRequestListResult page2 = sut.getSellerRequests(SELLER_ID, 1, 2);

        // then
        assertThat(page1.content()).hasSize(2);
        assertThat(page1.totalElements()).isEqualTo(5);
        assertThat(page1.totalPages()).isEqualTo(3);
        assertThat(page1.last()).isFalse();

        assertThat(page2.content()).hasSize(2);
        assertThat(page2.last()).isFalse();
    }

    // ─── getSellerRequestDetail ───

    @Test
    @DisplayName("getSellerRequestDetail() — 정상 조회")
    void 상세_정상_조회() {
        // given
        createShop();
        CurationRequest request = createRequest(NEAR_LAT, NEAR_LNG);

        // when
        SellerRequestDetailResult result = sut.getSellerRequestDetail(request.getId(), SELLER_ID);

        // then
        assertThat(result.requestId()).isEqualTo(request.getId());
        assertThat(result.status().name()).isEqualTo("OPEN");
        assertThat(result.myProposalId()).isNull();
    }

    @Test
    @DisplayName("getSellerRequestDetail() — myProposalId 포함")
    void 상세_myProposalId_포함() {
        // given
        FlowerShop shop = createShop();
        CurationRequest request = createRequest(NEAR_LAT, NEAR_LNG);
        Proposal proposal = Proposal.create(request.getId(), shop.getId(), FIXED_CLOCK);
        Proposal saved = proposalRepository.save(proposal);

        // when
        SellerRequestDetailResult result = sut.getSellerRequestDetail(request.getId(), SELLER_ID);

        // then
        assertThat(result.myProposalId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("getSellerRequestDetail() — 반경 밖 요청은 REQUEST_NOT_FOUND")
    void 상세_반경_밖_요청_예외() {
        // given
        createShop();
        CurationRequest farRequest = createRequest(FAR_LAT, FAR_LNG);

        // when & then
        assertThatThrownBy(() -> sut.getSellerRequestDetail(farRequest.getId(), SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("getSellerRequestDetail() — 존재하지 않는 요청 REQUEST_NOT_FOUND")
    void 상세_존재하지_않는_요청_예외() {
        // given
        createShop();

        // when & then
        assertThatThrownBy(() -> sut.getSellerRequestDetail(999L, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("getSellerRequestDetail() — 꽃집 미등록 시 SHOP_NOT_FOUND")
    void 상세_꽃집_미등록_예외() {
        // when & then
        assertThatThrownBy(() -> sut.getSellerRequestDetail(1L, SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SHOP_NOT_FOUND);
    }
}
