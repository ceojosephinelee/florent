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
import com.florent.domain.seller.SellerStatsResult;
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

class SellerStatsServiceTest {

    private FakeFlowerShopRepository shopRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeProposalRepository proposalRepository;
    private FakeReservationRepository reservationRepository;
    private SellerStatsService service;
    private final Clock clock = Clock.systemDefaultZone();

    @BeforeEach
    void setUp() {
        shopRepository = new FakeFlowerShopRepository();
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        reservationRepository = new FakeReservationRepository(requestRepository, proposalRepository, shopRepository);
        service = new SellerStatsService(shopRepository, requestRepository, proposalRepository, reservationRepository, clock);
    }

    @Test
    @DisplayName("판매자 통계 조회 성공")
    void 통계_조회_성공() {
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

        Proposal proposal = proposalRepository.save(Proposal.create(request.getId(), shop.getId(), clock));
        Proposal submitted = Proposal.reconstitute(
                proposal.getId(), proposal.getRequestId(), proposal.getFlowerShopId(),
                ProposalStatus.SUBMITTED, "제안", List.of(), List.of(), List.of(),
                null, null, "설명", List.of(), "PICKUP_30M", "14:00",
                new BigDecimal("30000"), proposal.getCreatedAt(), proposal.getExpiresAt(),
                proposal.getCreatedAt());
        proposalRepository.save(submitted);

        Reservation reservation = reservationRepository.save(Reservation.create(
                request.getId(), submitted.getId(),
                "PICKUP", LocalDate.now().plusDays(3),
                "PICKUP_30M", "14:00",
                "서울 강남", new BigDecimal("37.5001"), new BigDecimal("127.0001"),
                clock));

        // when
        SellerStatsResult result = service.getStats(sellerId);

        // then
        assertThat(result.monthlyReceivedRequestCount()).isEqualTo(1);
        assertThat(result.monthlySubmittedProposalCount()).isEqualTo(1);
        assertThat(result.monthlyConfirmedReservationCount()).isEqualTo(1);
        assertThat(result.recentReservations()).hasSize(1);
        assertThat(result.recentReservations().get(0).conceptTitle()).isEqualTo("제안");
    }

    @Test
    @DisplayName("데이터 없는 판매자 통계는 모두 0")
    void 데이터_없는_판매자_통계() {
        // given
        Long sellerId = 1L;
        shopRepository.save(FlowerShop.create(
                sellerId, "플로렌트", null, null,
                "서울", new BigDecimal("37.5000"), new BigDecimal("127.0000")));

        // when
        SellerStatsResult result = service.getStats(sellerId);

        // then
        assertThat(result.monthlyReceivedRequestCount()).isZero();
        assertThat(result.monthlySubmittedProposalCount()).isZero();
        assertThat(result.monthlyConfirmedReservationCount()).isZero();
        assertThat(result.recentReservations()).isEmpty();
    }
}
