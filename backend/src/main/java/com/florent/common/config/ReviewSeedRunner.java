package com.florent.common.config;

import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.proposal.Proposal;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.reservation.Reservation;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import com.florent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class ReviewSeedRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final FlowerShopRepository shopRepository;
    private final CurationRequestRepository requestRepository;
    private final ProposalRepository proposalRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Value("${review.buyer.email}")
    private String buyerEmail;

    @Value("${review.buyer.password}")
    private String buyerPassword;

    @Value("${review.seller.email}")
    private String sellerEmail;

    @Value("${review.seller.password}")
    private String sellerPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail(buyerEmail).isPresent()) {
            log.info("[SEED] 리뷰 계정이 이미 존재합니다. 시드 스킵.");
            return;
        }

        log.info("[SEED] 앱스토어 심사용 테스트 데이터 생성 시작...");

        // 1. 판매자 계정 + 가게
        User sellerUser = userRepository.save(
                User.createFromEmail(sellerEmail, passwordEncoder.encode(sellerPassword), "플로렌트판매자"));
        sellerUser.assignRole(UserRole.SELLER);
        userRepository.save(sellerUser);

        Seller seller = sellerRepository.save(Seller.create(sellerUser.getId()));

        FlowerShop shop = shopRepository.save(FlowerShop.create(
                seller.getId(),
                "플로렌트 테스트 가게",
                "앱스토어 심사용 테스트 꽃집입니다",
                "010-0000-0000",
                "서울 강남구 테헤란로 152",
                new BigDecimal("37.498500"),
                new BigDecimal("127.028700")));

        log.info("[SEED] 판매자 계정 생성 완료: sellerId={}, shopId={}", seller.getId(), shop.getId());

        // 2. 구매자 계정
        User buyerUser = userRepository.save(
                User.createFromEmail(buyerEmail, passwordEncoder.encode(buyerPassword), "플로렌트구매자"));
        buyerUser.assignRole(UserRole.BUYER);
        userRepository.save(buyerUser);

        Buyer buyer = buyerRepository.save(Buyer.create(buyerUser.getId(), "플로렌트구매자"));

        log.info("[SEED] 구매자 계정 생성 완료: buyerId={}", buyer.getId());

        // 2-b. 보조 판매자 (제안 2건을 같은 요청에 넣기 위해 — 리뷰어에게 노출 안 됨)
        User subSellerUser = userRepository.save(
                User.createFromEmail("florent-sub-seller@internal.test",
                        passwordEncoder.encode("internal"), "보조판매자"));
        subSellerUser.assignRole(UserRole.SELLER);
        userRepository.save(subSellerUser);
        Seller subSeller = sellerRepository.save(Seller.create(subSellerUser.getId()));
        FlowerShop subShop = shopRepository.save(FlowerShop.create(
                subSeller.getId(), "로즈앤블룸", null, "010-1111-2222",
                "서울 강남구 역삼로 123",
                new BigDecimal("37.499000"), new BigDecimal("127.029000")));

        // 3. 요청 3건 (OPEN 1 + CONFIRMED 2)
        CurationRequest openRequest = createRequest(buyer.getId(), "OPEN");
        log.info("[SEED] OPEN 요청 생성: requestId={}", openRequest.getId());

        CurationRequest confirmedReq1 = createRequest(buyer.getId(), "CONFIRMED");
        CurationRequest confirmedReq2 = createRequest(buyer.getId(), "CONFIRMED");

        // 4. 제안서 — OPEN 요청에 2건 (각각 다른 가게, SUBMITTED)
        Proposal proposal1 = createProposal(openRequest.getId(), shop.getId(),
                "봄날의 파스텔 부케",
                "파스텔 톤의 장미, 카네이션, 안개꽃을 조합한 봄 느낌 부케입니다.",
                new BigDecimal("45000"), "PICKUP_30M", "14:00");
        log.info("[SEED] 제안1 생성: proposalId={}", proposal1.getId());

        Proposal proposal2 = createProposal(openRequest.getId(), subShop.getId(),
                "로맨틱 레드 로즈",
                "빨간 장미 20송이로 구성된 클래식 로맨틱 부케입니다.",
                new BigDecimal("65000"), "PICKUP_30M", "15:00");
        log.info("[SEED] 제안2 생성: proposalId={}", proposal2.getId());

        // 5. 제안서 — CONFIRMED 요청에 각 1건 (SELECTED)
        Proposal selectedProposal1 = createSelectedProposal(confirmedReq1.getId(), shop.getId(),
                "봄날의 파스텔 부케", new BigDecimal("45000"), "PICKUP_30M", "14:00");

        Proposal selectedProposal2 = createSelectedProposal(confirmedReq2.getId(), shop.getId(),
                "로맨틱 레드 로즈", new BigDecimal("65000"), "PICKUP_30M", "15:00");

        // 6. 예약 — PENDING_CONTACT 1건
        Reservation pendingRes = reservationRepository.save(Reservation.create(
                confirmedReq1.getId(), selectedProposal1.getId(),
                "PICKUP", LocalDate.now(clock).plusDays(3),
                "PICKUP_30M", "14:00",
                "서울 강남구 테헤란로 152",
                new BigDecimal("37.498500"), new BigDecimal("127.028700"),
                clock));
        log.info("[SEED] PENDING_CONTACT 예약 생성: reservationId={}", pendingRes.getId());

        // 7. 예약 — CONFIRMED 1건
        Reservation confirmedRes = Reservation.create(
                confirmedReq2.getId(), selectedProposal2.getId(),
                "PICKUP", LocalDate.now(clock).plusDays(5),
                "PICKUP_30M", "15:00",
                "서울 강남구 테헤란로 152",
                new BigDecimal("37.498500"), new BigDecimal("127.028700"),
                clock);
        confirmedRes.confirm(clock);
        reservationRepository.save(confirmedRes);
        log.info("[SEED] CONFIRMED 예약 생성: reservationId={}", confirmedRes.getId());

        log.info("[SEED] 앱스토어 심사용 테스트 데이터 생성 완료!");
    }

    private CurationRequest createRequest(Long buyerId, String targetStatus) {
        CreateRequestCommand cmd = new CreateRequestCommand(
                buyerId,
                List.of("생일"),
                List.of("연인"),
                List.of("로맨틱"),
                BudgetTier.TIER2,
                FulfillmentType.PICKUP,
                LocalDate.now(clock).plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00"),
                        new TimeSlot(SlotKind.PICKUP_30M, "15:00")),
                "서울 강남구 테헤란로 152",
                new BigDecimal("37.498500"),
                new BigDecimal("127.028700"),
                null);

        CurationRequest request = CurationRequest.create(cmd, clock);
        if ("CONFIRMED".equals(targetStatus)) {
            request.confirm();
        }
        return requestRepository.save(request);
    }

    private Proposal createProposal(Long requestId, Long shopId,
                                     String title, String description,
                                     BigDecimal price, String slotKind, String slotValue) {
        Proposal proposal = Proposal.create(requestId, shopId, clock);
        proposal.updateDraft(title,
                List.of("PINK", "WHITE"),
                List.of("장미", "카네이션"),
                List.of("리본"),
                null, null,
                description,
                List.of("https://placehold.co/400x400"),
                slotKind, slotValue, price);
        proposal.submit(clock);
        return proposalRepository.save(proposal);
    }

    private Proposal createSelectedProposal(Long requestId, Long shopId,
                                             String title, BigDecimal price,
                                             String slotKind, String slotValue) {
        Proposal proposal = Proposal.create(requestId, shopId, clock);
        proposal.updateDraft(title,
                List.of("PINK", "WHITE"),
                List.of("장미", "카네이션"),
                List.of("리본"),
                null, null,
                title + " — 정성껏 준비하겠습니다.",
                List.of("https://placehold.co/400x400"),
                slotKind, slotValue, price);
        proposal.submit(clock);
        proposal.select();
        return proposalRepository.save(proposal);
    }
}
