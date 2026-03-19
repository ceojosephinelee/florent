package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerProfileResult;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRole;
import com.florent.fake.FakeBuyerRepository;
import com.florent.fake.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuyerProfileServiceTest {

    private FakeBuyerRepository buyerRepository;
    private FakeUserRepository userRepository;
    private BuyerProfileService service;

    @BeforeEach
    void setUp() {
        buyerRepository = new FakeBuyerRepository();
        userRepository = new FakeUserRepository();
        service = new BuyerProfileService(buyerRepository, userRepository);
    }

    @Test
    @DisplayName("구매자 프로필 조회 성공")
    void 프로필_조회_성공() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao1", "buyer@test.com", "구매자닉네임"));
        user.assignRole(UserRole.BUYER);
        user = userRepository.save(user);
        Buyer buyer = buyerRepository.save(Buyer.create(user.getId(), "구매자닉네임"));

        // when
        BuyerProfileResult result = service.getProfile(buyer.getId(), user.getId());

        // then
        assertThat(result.buyerId()).isEqualTo(buyer.getId());
        assertThat(result.nickName()).isEqualTo("구매자닉네임");
        assertThat(result.email()).isEqualTo("buyer@test.com");
        assertThat(result.role()).isEqualTo("BUYER");
    }

    @Test
    @DisplayName("존재하지 않는 구매자 조회 시 예외")
    void 존재하지_않는_구매자_예외() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao1", null, null));

        // when & then
        assertThatThrownBy(() -> service.getProfile(999L, user.getId()))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.USER_NOT_FOUND));
    }
}
