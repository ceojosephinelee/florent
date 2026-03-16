package com.florent.domain.seller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SellerTest {

    @Test
    @DisplayName("create — userId로 판매자가 생성된다")
    void create_판매자_생성() {
        // given
        Long userId = 1L;

        // when
        Seller seller = Seller.create(userId);

        // then
        assertThat(seller.getUserId()).isEqualTo(userId);
        assertThat(seller.getId()).isNull();
    }

    @Test
    @DisplayName("reconstitute — DB 재구성이 정상적으로 동작한다")
    void reconstitute_DB_재구성() {
        // given & when
        Seller seller = Seller.reconstitute(1L, 10L);

        // then
        assertThat(seller.getId()).isEqualTo(1L);
        assertThat(seller.getUserId()).isEqualTo(10L);
    }
}
