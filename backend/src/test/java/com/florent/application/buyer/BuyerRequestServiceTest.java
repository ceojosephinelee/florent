package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.RequestDetailResult;
import com.florent.domain.request.RequestListResult;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeProposalCountPort;
import com.florent.fake.FakeSaveNotificationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.florent.support.TestFixtures;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyerRequestServiceTest {

    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeSaveNotificationUseCase notificationPort;
    private FakeProposalCountPort proposalCountPort;
    private BuyerRequestService sut;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        shopRepository = new FakeFlowerShopRepository();
        notificationPort = new FakeSaveNotificationUseCase();
        proposalCountPort = new FakeProposalCountPort();
        sut = new BuyerRequestService(requestRepository, shopRepository, notificationPort, proposalCountPort, fixedClock);
    }

    private CreateRequestCommand defaultCommand() {
        return new CreateRequestCommand(
                1L,
                List.of("생일"),
                List.of("연인"),
                List.of("로맨틱"),
                BudgetTier.TIER2,
                FulfillmentType.DELIVERY,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.DELIVERY_WINDOW, "14:00-16:00")),
                "서울시 강남구",
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610")
        );
    }

    // ─── create ───

    @Test
    @DisplayName("요청이 저장되고 결과가 반환된다")
    void 요청_저장_확인() {
        // given
        CreateRequestCommand command = defaultCommand();

        // when
        CreateRequestResult result = sut.create(command);

        // then
        assertThat(result.requestId()).isNotNull();
        assertThat(result.status()).isEqualTo("OPEN");
        assertThat(requestRepository.findById(result.requestId())).isPresent();
    }

    @Test
    @DisplayName("반경 2km 내 꽃집에만 알림이 발송된다")
    void 반경_내_꽃집에만_알림_발송() {
        // given — 기준점: 37.498095, 127.027610
        // 약 1km 거리 꽃집
        FlowerShop nearbyShop = FlowerShop.reconstitute(
                1L, 10L, "가까운꽃집", null, null, "서울시 강남구",
                new BigDecimal("37.505"),
                new BigDecimal("127.027"));
        // 약 10km 거리 꽃집
        FlowerShop farShop = FlowerShop.reconstitute(
                2L, 20L, "먼꽃집", null, null, "서울시 마포구",
                new BigDecimal("37.560"),
                new BigDecimal("126.920"));
        shopRepository.save(nearbyShop);
        shopRepository.save(farShop);

        // when
        sut.create(defaultCommand());

        // then
        assertThat(notificationPort.getRecords()).hasSize(1);
        assertThat(notificationPort.getRecords().get(0).sellerId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("반경 밖 꽃집에는 알림이 발송되지 않는다")
    void 반경_밖_꽃집_알림_미발송() {
        // given — 모든 꽃집이 반경 밖
        FlowerShop farShop = FlowerShop.reconstitute(
                1L, 10L, "먼꽃집", null, null, "서울시 마포구",
                new BigDecimal("37.560"),
                new BigDecimal("126.920"));
        shopRepository.save(farShop);

        // when
        sut.create(defaultCommand());

        // then
        assertThat(notificationPort.getRecords()).isEmpty();
    }

    @Test
    @DisplayName("꽃집이 0개일 때 정상 완료된다")
    void 꽃집_0개일_때_정상_완료() {
        // given — 꽃집 없음

        // when
        CreateRequestResult result = sut.create(defaultCommand());

        // then
        assertThat(result.requestId()).isNotNull();
        assertThat(notificationPort.getRecords()).isEmpty();
    }

    // ─── getList ───

    @Test
    @DisplayName("요청 목록 조회 시 proposalCount가 올바르게 매핑된다")
    void getList_정상_조회_proposalCount_매핑_포함() {
        // given
        CreateRequestResult created = sut.create(defaultCommand());
        proposalCountPort.setCount(created.requestId(), ProposalStatus.DRAFT, 3);
        proposalCountPort.setCount(created.requestId(), ProposalStatus.SUBMITTED, 1);

        // when
        RequestListResult result = sut.getList(1L, 0, 20);

        // then
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).draftProposalCount()).isEqualTo(3);
        assertThat(result.content().get(0).submittedProposalCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("요청이 없으면 빈 목록이 반환된다")
    void getList_빈_목록() {
        // given — 요청 없음

        // when
        RequestListResult result = sut.getList(1L, 0, 20);

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    // ─── getDetail ───

    @Test
    @DisplayName("요청 상세 조회 시 정상 응답된다")
    void getDetail_정상_조회() {
        // given
        CreateRequestResult created = sut.create(defaultCommand());
        proposalCountPort.setCount(created.requestId(), ProposalStatus.DRAFT, 2);
        proposalCountPort.setCount(created.requestId(), ProposalStatus.SUBMITTED, 5);

        // when
        RequestDetailResult result = sut.getDetail(created.requestId(), 1L);

        // then
        assertThat(result.requestId()).isEqualTo(created.requestId());
        assertThat(result.draftProposalCount()).isEqualTo(2);
        assertThat(result.submittedProposalCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("존재하지 않는 요청을 조회하면 REQUEST_NOT_FOUND 예외가 발생한다")
    void getDetail_존재하지_않는_요청_REQUEST_NOT_FOUND() {
        // given — 요청 없음

        // when & then
        assertThatThrownBy(() -> sut.getDetail(999L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.REQUEST_NOT_FOUND);
    }

    @Test
    @DisplayName("타인의 요청을 조회하면 FORBIDDEN 예외가 발생한다")
    void getDetail_타인_요청_FORBIDDEN() {
        // given
        CreateRequestResult created = sut.create(defaultCommand());

        // when & then — buyerId=1L로 생성, buyerId=999L로 조회
        assertThatThrownBy(() -> sut.getDetail(created.requestId(), 999L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.FORBIDDEN);
    }
}
