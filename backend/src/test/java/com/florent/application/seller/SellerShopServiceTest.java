package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.RegisterShopCommand;
import com.florent.domain.shop.RegisterShopResult;
import com.florent.domain.shop.ShopDetailResult;
import com.florent.domain.shop.UpdateShopCommand;
import com.florent.fake.FakeFlowerShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SellerShopServiceTest {

    private FakeFlowerShopRepository shopRepository;
    private SellerShopService sut;

    private static final Long SELLER_ID = 1L;

    @BeforeEach
    void setUp() {
        shopRepository = new FakeFlowerShopRepository();
        sut = new SellerShopService(shopRepository);
    }

    // ─── register ───

    @Test
    @DisplayName("register() — 꽃집 등록 성공")
    void register_정상_등록() {
        // given
        RegisterShopCommand command = new RegisterShopCommand(
                "플로렌트", "예쁜 꽃집", "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // when
        RegisterShopResult result = sut.register(SELLER_ID, command);

        // then
        assertThat(result.shopId()).isNotNull();
        assertThat(result.name()).isEqualTo("플로렌트");
    }

    @Test
    @DisplayName("register() — 이미 등록된 판매자는 SHOP_ALREADY_EXISTS")
    void register_중복_등록_예외() {
        // given
        shopRepository.save(FlowerShop.create(
                SELLER_ID, "기존가게", null, null, "서울시",
                new BigDecimal("37.498"), new BigDecimal("127.027")));

        RegisterShopCommand command = new RegisterShopCommand(
                "새가게", null, null, "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // when & then
        assertThatThrownBy(() -> sut.register(SELLER_ID, command))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SHOP_ALREADY_EXISTS);
    }

    // ─── getShop ───

    @Test
    @DisplayName("getShop() — 꽃집 조회 성공")
    void getShop_정상_조회() {
        // given
        shopRepository.save(FlowerShop.create(
                SELLER_ID, "플로렌트", "예쁜 꽃집", "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));

        // when
        ShopDetailResult result = sut.getShop(SELLER_ID);

        // then
        assertThat(result.name()).isEqualTo("플로렌트");
        assertThat(result.description()).isEqualTo("예쁜 꽃집");
        assertThat(result.phone()).isEqualTo("010-1234-5678");
        assertThat(result.addressText()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("getShop() — 미등록 판매자는 SHOP_NOT_FOUND")
    void getShop_미등록_예외() {
        // when & then
        assertThatThrownBy(() -> sut.getShop(SELLER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SHOP_NOT_FOUND);
    }

    // ─── update ───

    @Test
    @DisplayName("update() — 꽃집 정보 수정 성공")
    void update_정상_수정() {
        // given
        shopRepository.save(FlowerShop.create(
                SELLER_ID, "원래이름", null, null,
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));

        UpdateShopCommand command = new UpdateShopCommand(
                "새이름", "새로운 설명", "010-9999-9999", null, null, null);

        // when
        ShopDetailResult result = sut.update(SELLER_ID, command);

        // then
        assertThat(result.name()).isEqualTo("새이름");
        assertThat(result.description()).isEqualTo("새로운 설명");
        assertThat(result.phone()).isEqualTo("010-9999-9999");
        assertThat(result.addressText()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("update() — 미등록 판매자는 SHOP_NOT_FOUND")
    void update_미등록_예외() {
        // given
        UpdateShopCommand command = new UpdateShopCommand("새이름", null, null, null, null, null);

        // when & then
        assertThatThrownBy(() -> sut.update(SELLER_ID, command))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SHOP_NOT_FOUND);
    }
}
