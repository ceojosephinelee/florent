package com.florent.domain.buyer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BuyerTest {

    @Test
    @DisplayName("reconstitute() — 모든 필드가 올바르게 복원된다")
    void reconstitute_모든_필드_복원() {
        // given & when
        Buyer buyer = Buyer.reconstitute(1L, 100L, "테스트닉네임");

        // then
        assertThat(buyer.getId()).isEqualTo(1L);
        assertThat(buyer.getUserId()).isEqualTo(100L);
        assertThat(buyer.getNickName()).isEqualTo("테스트닉네임");
    }
}
