package com.florent.domain.shop;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FlowerShopTest {

    @Test
    @DisplayName("create() — 필수 필드로 꽃집이 생성된다")
    void create_정상_생성() {
        // given
        Long sellerId = 1L;

        // when
        FlowerShop shop = FlowerShop.create(
                sellerId, "플로렌트", "예쁜 꽃집입니다", "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // then
        assertThat(shop.getSellerId()).isEqualTo(sellerId);
        assertThat(shop.getShopName()).isEqualTo("플로렌트");
        assertThat(shop.getShopDescription()).isEqualTo("예쁜 꽃집입니다");
        assertThat(shop.getShopPhone()).isEqualTo("010-1234-5678");
        assertThat(shop.getShopAddress()).isEqualTo("서울시 강남구");
    }

    @Test
    @DisplayName("create() — description, phone은 null 허용")
    void create_nullable_필드_허용() {
        // given & when
        FlowerShop shop = FlowerShop.create(
                1L, "플로렌트", null, null,
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // then
        assertThat(shop.getShopDescription()).isNull();
        assertThat(shop.getShopPhone()).isNull();
    }

    @Test
    @DisplayName("create() — sellerId null이면 NullPointerException")
    void create_sellerId_null_예외() {
        // when & then
        assertThatThrownBy(() -> FlowerShop.create(
                null, "플로렌트", null, null,
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("update() — non-null 필드만 변경된다")
    void update_부분_수정() {
        // given
        FlowerShop shop = FlowerShop.create(
                1L, "원래이름", "원래설명", "010-0000-0000",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // when
        shop.update("새이름", null, "010-1111-1111", null, null, null);

        // then
        assertThat(shop.getShopName()).isEqualTo("새이름");
        assertThat(shop.getShopDescription()).isEqualTo("원래설명");
        assertThat(shop.getShopPhone()).isEqualTo("010-1111-1111");
        assertThat(shop.getShopAddress()).isEqualTo("서울시 강남구");
        assertThat(shop.getShopLat()).isEqualByComparingTo(new BigDecimal("37.498095"));
    }

    @Test
    @DisplayName("update() — 모든 필드 변경 가능")
    void update_전체_수정() {
        // given
        FlowerShop shop = FlowerShop.create(
                1L, "원래이름", null, null,
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // when
        shop.update("새이름", "새설명", "010-9999-9999",
                "서울시 서초구", new BigDecimal("37.500000"), new BigDecimal("127.030000"));

        // then
        assertThat(shop.getShopName()).isEqualTo("새이름");
        assertThat(shop.getShopDescription()).isEqualTo("새설명");
        assertThat(shop.getShopPhone()).isEqualTo("010-9999-9999");
        assertThat(shop.getShopAddress()).isEqualTo("서울시 서초구");
        assertThat(shop.getShopLat()).isEqualByComparingTo(new BigDecimal("37.500000"));
        assertThat(shop.getShopLng()).isEqualByComparingTo(new BigDecimal("127.030000"));
    }

    @Test
    @DisplayName("reconstitute() — DB 재구성 시 모든 필드 복원")
    void reconstitute_전체_필드_복원() {
        // when
        FlowerShop shop = FlowerShop.reconstitute(
                100L, 1L, "플로렌트", "예쁜 꽃집", "010-1234-5678",
                "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610"));

        // then
        assertThat(shop.getId()).isEqualTo(100L);
        assertThat(shop.getSellerId()).isEqualTo(1L);
        assertThat(shop.getShopName()).isEqualTo("플로렌트");
        assertThat(shop.getShopDescription()).isEqualTo("예쁜 꽃집");
    }
}
