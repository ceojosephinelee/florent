package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CurationRequest;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.seller.Seller;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRole;
import com.florent.fake.FakeBuyerRepository;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeNotificationRepository;
import com.florent.fake.FakeOutboxEventRepository;
import com.florent.fake.FakePaymentRepository;
import com.florent.fake.FakeProposalRepository;
import com.florent.fake.FakeReservationRepository;
import com.florent.fake.FakeSellerRepository;
import com.florent.fake.FakeUserDeviceRepository;
import com.florent.fake.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WithdrawServiceTest {

    private WithdrawService withdrawService;
    private FakeUserRepository userRepository;
    private FakeBuyerRepository buyerRepository;
    private FakeSellerRepository sellerRepository;
    private FakeFlowerShopRepository flowerShopRepository;
    private FakeCurationRequestRepository requestRepository;
    private FakeProposalRepository proposalRepository;
    private FakeReservationRepository reservationRepository;
    private FakePaymentRepository paymentRepository;
    private FakeNotificationRepository notificationRepository;
    private FakeOutboxEventRepository outboxEventRepository;
    private FakeUserDeviceRepository userDeviceRepository;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        buyerRepository = new FakeBuyerRepository();
        sellerRepository = new FakeSellerRepository();
        flowerShopRepository = new FakeFlowerShopRepository();
        requestRepository = new FakeCurationRequestRepository();
        proposalRepository = new FakeProposalRepository();
        reservationRepository = new FakeReservationRepository(
                requestRepository, proposalRepository, flowerShopRepository);
        paymentRepository = new FakePaymentRepository();
        notificationRepository = new FakeNotificationRepository();
        outboxEventRepository = new FakeOutboxEventRepository();
        userDeviceRepository = new FakeUserDeviceRepository();

        withdrawService = new WithdrawService(
                userRepository, buyerRepository, sellerRepository,
                flowerShopRepository, requestRepository, proposalRepository,
                reservationRepository, paymentRepository,
                notificationRepository, outboxEventRepository, userDeviceRepository);
    }

    @Test
    @DisplayName("구매자 탈퇴 시 사용자와 구매자 데이터가 모두 삭제된다")
    void 구매자_탈퇴_성공() {
        // given
        User user = userRepository.save(
                User.createFromKakao("kakao123", "test@test.com", "테스트유저"));
        user.assignRole(UserRole.BUYER);
        user = userRepository.save(user);
        Buyer buyer = buyerRepository.save(Buyer.create(user.getId(), "테스트유저"));

        // when
        withdrawService.withdraw(user.getId());

        // then
        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(buyerRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("판매자 탈퇴 시 사용자, 판매자, 꽃집 데이터가 모두 삭제된다")
    void 판매자_탈퇴_성공() {
        // given
        User user = userRepository.save(
                User.createFromKakao("kakao456", "seller@test.com", "판매자"));
        user.assignRole(UserRole.SELLER);
        user = userRepository.save(user);
        Seller seller = sellerRepository.save(Seller.create(user.getId()));
        flowerShopRepository.save(FlowerShop.create(
                seller.getId(), "테스트꽃집", null, null,
                "서울시 강남구", BigDecimal.valueOf(37.5), BigDecimal.valueOf(127.0)));

        // when
        withdrawService.withdraw(user.getId());

        // then
        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(sellerRepository.findByUserId(user.getId())).isEmpty();
        assertThat(flowerShopRepository.findBySellerId(seller.getId())).isEmpty();
    }

    @Test
    @DisplayName("구매자 탈퇴 시 요청과 관련 제안이 모두 삭제된다")
    void 구매자_탈퇴_시_요청_제안_삭제() {
        // given
        User user = userRepository.save(
                User.createFromKakao("kakao789", "buyer@test.com", "구매자"));
        user.assignRole(UserRole.BUYER);
        user = userRepository.save(user);
        Buyer buyer = buyerRepository.save(Buyer.create(user.getId(), "구매자"));

        CreateRequestCommand cmd = new CreateRequestCommand(
                buyer.getId(), List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER1, FulfillmentType.PICKUP,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "10:00")),
                "서울시 강남구", BigDecimal.valueOf(37.5), BigDecimal.valueOf(127.0),
                null);
        CurationRequest request = requestRepository.save(
                CurationRequest.create(cmd, java.time.Clock.systemDefaultZone()));

        // when
        withdrawService.withdraw(user.getId());

        // then
        assertThat(userRepository.findById(user.getId())).isEmpty();
        assertThat(requestRepository.findIdsByBuyerId(buyer.getId())).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 탈퇴 시 예외가 발생한다")
    void 존재하지_않는_사용자_탈퇴_실패() {
        // given
        Long nonExistentUserId = 999L;

        // when & then
        assertThatThrownBy(() -> withdrawService.withdraw(nonExistentUserId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("역할이 없는 사용자도 탈퇴할 수 있다")
    void 역할_없는_사용자_탈퇴_성공() {
        // given
        User user = userRepository.save(
                User.createFromKakao("kakao000", "norole@test.com", "무역할"));

        // when
        withdrawService.withdraw(user.getId());

        // then
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
}
