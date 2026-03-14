package com.florent.domain.proposal;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProposalTest {

    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-03-15T10:00:00Z"), ZoneId.of("Asia/Seoul"));

    @Test
    @DisplayName("create() — DRAFT 상태이고 expiresAt은 24시간 후다")
    void create_상태는_DRAFT이고_만료시각은_24시간_후다() {
        // given & when
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.DRAFT);
        assertThat(proposal.getRequestId()).isEqualTo(1L);
        assertThat(proposal.getFlowerShopId()).isEqualTo(10L);
        assertThat(proposal.getExpiresAt()).isEqualTo(proposal.getCreatedAt().plusHours(24));
    }

    @Test
    @DisplayName("submit() — DRAFT에서 SUBMITTED로 전이하고 submittedAt이 설정된다")
    void submit_정상_전이() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // when
        proposal.submit(fixedClock);

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.SUBMITTED);
        assertThat(proposal.getSubmittedAt()).isNotNull();
    }

    @Test
    @DisplayName("submit() — DRAFT가 아닐 때 PROPOSAL_NOT_SUBMITTABLE 예외")
    void submit_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.submit(fixedClock);

        // when & then
        assertThatThrownBy(() -> proposal.submit(fixedClock))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_SUBMITTABLE));
    }

    @Test
    @DisplayName("select() — SUBMITTED에서 SELECTED로 전이한다")
    void select_정상_전이() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.submit(fixedClock);

        // when
        proposal.select();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.SELECTED);
    }

    @Test
    @DisplayName("select() — SUBMITTED가 아닐 때 PROPOSAL_NOT_SELECTABLE 예외")
    void select_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // when & then
        assertThatThrownBy(proposal::select)
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_SELECTABLE));
    }

    @Test
    @DisplayName("markNotSelected() — SUBMITTED에서 NOT_SELECTED로 전이한다")
    void markNotSelected_정상_전이() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.submit(fixedClock);

        // when
        proposal.markNotSelected();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.NOT_SELECTED);
    }

    @Test
    @DisplayName("markNotSelected() — SUBMITTED가 아닐 때 PROPOSAL_NOT_SELECTABLE 예외")
    void markNotSelected_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // when & then
        assertThatThrownBy(proposal::markNotSelected)
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_SELECTABLE));
    }

    @Test
    @DisplayName("expire() — SELECTED 상태이면 상태가 변경되지 않는다")
    void expire_SELECTED일_때_무시() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.submit(fixedClock);
        proposal.select();

        // when
        proposal.expire();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.SELECTED);
    }

    @Test
    @DisplayName("isExpired() — EXPIRED 상태이면 true")
    void isExpired_EXPIRED_상태이면_true() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.expire();

        // when & then
        assertThat(proposal.isExpired(fixedClock)).isTrue();
    }

    @Test
    @DisplayName("isExpired() — DRAFT이고 만료시각이 지나면 true")
    void isExpired_DRAFT이고_만료시각_지나면_true() {
        // given
        Proposal proposal = Proposal.reconstitute(
                1L, 1L, 10L, ProposalStatus.DRAFT,
                null, null, null, null, null, null,
                "설명", null, "PICKUP_30M", "14:00",
                new BigDecimal("30000"),
                LocalDateTime.now(fixedClock).minusHours(25),
                LocalDateTime.now(fixedClock).minusHours(1),
                null
        );

        // when & then
        assertThat(proposal.isExpired(fixedClock)).isTrue();
    }

    @Test
    @DisplayName("isExpired() — SUBMITTED이고 만료시각 전이면 false")
    void isExpired_SUBMITTED이고_만료시각_전이면_false() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);
        proposal.submit(fixedClock);

        // when & then
        assertThat(proposal.isExpired(fixedClock)).isFalse();
    }

    @Test
    @DisplayName("isVisibleToBuyer() — SUBMITTED, EXPIRED, SELECTED, NOT_SELECTED만 true")
    void isVisibleToBuyer_상태별_검증() {
        // given
        Proposal draft = Proposal.create(1L, 10L, fixedClock);
        Proposal submitted = Proposal.create(1L, 11L, fixedClock);
        submitted.submit(fixedClock);
        Proposal selected = Proposal.create(1L, 12L, fixedClock);
        selected.submit(fixedClock);
        selected.select();
        Proposal expired = Proposal.create(1L, 13L, fixedClock);
        expired.expire();

        // then
        assertThat(draft.isVisibleToBuyer()).isFalse();
        assertThat(submitted.isVisibleToBuyer()).isTrue();
        assertThat(selected.isVisibleToBuyer()).isTrue();
        assertThat(expired.isVisibleToBuyer()).isTrue();
    }
}
