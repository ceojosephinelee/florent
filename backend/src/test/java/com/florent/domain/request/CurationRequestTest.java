package com.florent.domain.request;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurationRequestTest {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-03-15T10:00:00Z"), ZoneId.of("Asia/Seoul"));

    private CreateRequestCommand defaultCommand() {
        return new CreateRequestCommand(
                1L,
                List.of("생일"),
                List.of("연인"),
                List.of("로맨틱"),
                BudgetTier.TIER2,
                FulfillmentType.DELIVERY,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.DELIVERY_WINDOW, "14:00-16:00")),
                "서울시 강남구",
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610")
        );
    }

    @Test
    @DisplayName("create() — 상태는 OPEN이고 만료시각은 생성시각 + 48시간이다")
    void create_상태는_OPEN이고_만료시각은_48시간_후다() {
        // given
        CreateRequestCommand command = defaultCommand();

        // when
        CurationRequest request = CurationRequest.create(command, fixedClock);

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.OPEN);
        assertThat(request.getCreatedAt()).isNotNull();
        assertThat(request.getExpiresAt()).isEqualTo(request.getCreatedAt().plusHours(48));
        assertThat(request.getBuyerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("confirm() — OPEN 상태에서 CONFIRMED로 전이한다")
    void confirm_정상_전이() {
        // given
        CurationRequest request = CurationRequest.create(defaultCommand(), fixedClock);

        // when
        request.confirm();

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
    }

    @Test
    @DisplayName("confirm() — OPEN이 아닌 상태에서 BusinessException이 발생한다")
    void confirm_잘못된_상태에서_예외_발생() {
        // given
        CurationRequest request = CurationRequest.create(defaultCommand(), fixedClock);
        request.expire();

        // when & then
        assertThatThrownBy(request::confirm)
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.REQUEST_NOT_OPEN));
    }

    @Test
    @DisplayName("expire() — OPEN 상태에서 EXPIRED로 전이한다")
    void expire_정상_전이() {
        // given
        CurationRequest request = CurationRequest.create(defaultCommand(), fixedClock);

        // when
        request.expire();

        // then
        assertThat(request.getStatus()).isEqualTo(RequestStatus.EXPIRED);
    }

    @Test
    @DisplayName("isExpired() — EXPIRED 상태이면 true를 반환한다")
    void isExpired_EXPIRED_상태이면_true() {
        // given
        CurationRequest request = CurationRequest.create(defaultCommand(), fixedClock);
        request.expire();

        // when & then
        assertThat(request.isExpired(fixedClock)).isTrue();
    }

    @Test
    @DisplayName("isExpired() — OPEN이고 만료시각이 지나면 true를 반환한다")
    void isExpired_OPEN이고_만료시각_지나면_true() {
        // given
        CurationRequest request = CurationRequest.reconstitute(
                1L, 1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.DELIVERY,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.DELIVERY_WINDOW, "14:00-16:00")),
                "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                LocalDateTime.now(fixedClock).minusHours(49),
                LocalDateTime.now(fixedClock).minusHours(1)
        );

        // when & then
        assertThat(request.isExpired(fixedClock)).isTrue();
    }

    @Test
    @DisplayName("isExpired() — OPEN이고 만료시각 전이면 false를 반환한다")
    void isExpired_OPEN이고_만료시각_전이면_false() {
        // given
        CurationRequest request = CurationRequest.create(defaultCommand(), fixedClock);

        // when & then
        assertThat(request.isExpired(fixedClock)).isFalse();
    }
}
