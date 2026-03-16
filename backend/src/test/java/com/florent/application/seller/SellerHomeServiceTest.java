package com.florent.application.seller;

import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.reservation.Reservation;
import com.florent.domain.seller.SellerHomeResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeProposalRepository;
import com.florent.fake.FakeReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SellerHomeServiceTest {

    private FakeFlowerShopRepository shopRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeProposalRepository proposalRepository;
    private FakeReservationRepository reservationRepository;
    private SellerHomeService service;
    private final Clock clock = Clock.systemDefaultZone();

    @BeforeEach
    void setUp() {
        shopRepository = new FakeFlowerShopRepository();
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        reservationRepository = new FakeReservationRepository(requestRepository, proposalRepository, shopRepository);
        service = new SellerHomeService(shopRepository, requestRepository, proposalRepository, reservationRepository);
    }

    @Test
    @DisplayName("판매자 홈 대시보드 조회 성공")
    void 홈_대시보드_조회_성공() {
        // given
        Long sellerId = 1L;
        FlowerShop shop = shopRepository.save(FlowerShop.create(
                sellerId, "플로렌트", null, null,
                "서울", new BigDecimal("37.5000"), new BigDecimal("127.0000")));

        CurationRequest request = requestRepository.save(CurationRequest.create(
                new CreateRequestCommand(
                        1L, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                        BudgetTier.TIER2, FulfillmentType.PICKUP,
                        LocalDate.now().plusDays(3),
                        List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                        "서울 강남", new BigDecimal("37.5001"), new BigDecimal("127.0001")),
                clock));

        Proposal draft = proposalRepository.save(Proposal.create(request.getId(), shop.getId(), clock));
        Proposal submitted = proposalRepository.save(Proposal.create(request.getId() + 100, shop.getId(), clock));
        // Simulate submit
        Proposal submittedP = Proposal.reconstitute(
                submitted.getId(), submitted.getRequestId(), submitted.getFlowerShopId(),
                ProposalStatus.SUBMITTED, "제안", List.of(), List.of(), List.of(),
                null, null, "설명", List.of(), "PICKUP_30M", "14:00",
                new BigDecimal("30000"), submitted.getCreatedAt(), submitted.getExpiresAt(),
                submitted.getCreatedAt());
        proposalRepository.save(submittedP);

        // when
        SellerHomeResult result = service.getHome(sellerId);

        // then
        assertThat(result.openRequestCount()).isEqualTo(1);
        assertThat(result.draftProposalCount()).isEqualTo(1);
        assertThat(result.submittedProposalCount()).isEqualTo(1);
        assertThat(result.recentRequests()).hasSize(1);
    }

    @Test
    @DisplayName("반경 밖 요청은 대시보드에 포함되지 않는다")
    void 반경_밖_요청_미포함() {
        // given
        Long sellerId = 1L;
        shopRepository.save(FlowerShop.create(
                sellerId, "플로렌트", null, null,
                "서울", new BigDecimal("37.5000"), new BigDecimal("127.0000")));

        requestRepository.save(CurationRequest.create(
                new CreateRequestCommand(
                        1L, List.of("생일"), List.of("연인"), List.of("로맨틱"),
                        BudgetTier.TIER2, FulfillmentType.PICKUP,
                        LocalDate.now().plusDays(3),
                        List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                        "부산", new BigDecimal("35.0000"), new BigDecimal("129.0000")),
                clock));

        // when
        SellerHomeResult result = service.getHome(sellerId);

        // then
        assertThat(result.openRequestCount()).isZero();
        assertThat(result.recentRequests()).isEmpty();
    }
}
