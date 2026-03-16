package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerProfileResult;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRole;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeSellerRepository;
import com.florent.fake.FakeUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerProfileServiceTest {

    private FakeSellerRepository sellerRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeUserRepository userRepository;
    private SellerProfileService service;

    @BeforeEach
    void setUp() {
        sellerRepository = new FakeSellerRepository();
        shopRepository = new FakeFlowerShopRepository();
        userRepository = new FakeUserRepository();
        service = new SellerProfileService(sellerRepository, shopRepository, userRepository);
    }

    @Test
    @DisplayName("판매자 프로필 조회 성공")
    void 프로필_조회_성공() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao1", "seller@test.com"));
        user.assignRole(UserRole.SELLER);
        user = userRepository.save(user);
        Seller seller = sellerRepository.save(Seller.create(user.getId()));
        shopRepository.save(FlowerShop.create(
                seller.getId(), "플로렌트", null, null,
                "서울시 강남구", new BigDecimal("37.5"), new BigDecimal("127.0")));

        // when
        SellerProfileResult result = service.getProfile(seller.getId());

        // then
        assertThat(result.sellerId()).isEqualTo(seller.getId());
        assertThat(result.shopName()).isEqualTo("플로렌트");
        assertThat(result.shopAddress()).isEqualTo("서울시 강남구");
        assertThat(result.role()).isEqualTo("SELLER");
    }

    @Test
    @DisplayName("꽃집 미등록 판매자 프로필 조회 시 예외")
    void 꽃집_미등록_예외() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao1", null));
        user.assignRole(UserRole.SELLER);
        user = userRepository.save(user);
        Seller seller = sellerRepository.save(Seller.create(user.getId()));

        // when & then
        assertThatThrownBy(() -> service.getProfile(seller.getId()))
                .isInstanceOf(BusinessException.class)
                .satisfies(e -> assertThat(((BusinessException) e).getErrorCode())
                        .isEqualTo(ErrorCode.SHOP_NOT_FOUND));
    }
}
