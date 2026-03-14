package com.florent.domain.proposal;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProposalTest {

    @Test
    @DisplayName("create() — DRAFT 상태이고 expiresAt은 24시간 후다")
    void create_상태는_DRAFT이고_만료시각은_24시간_후다() {
        // given & when
        Proposal proposal = Proposal.create(1L, 10L);

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
        Proposal proposal = Proposal.create(1L, 10L);

        // when
        proposal.submit();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.SUBMITTED);
        assertThat(proposal.getSubmittedAt()).isNotNull();
    }

    @Test
    @DisplayName("submit() — DRAFT가 아닐 때 PROPOSAL_NOT_SUBMITTABLE 예외")
    void submit_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.submit();

        // when & then
        assertThatThrownBy(proposal::submit)
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_SUBMITTABLE));
    }

    @Test
    @DisplayName("select() — SUBMITTED에서 SELECTED로 전이한다")
    void select_정상_전이() {
        // given
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.submit();

        // when
        proposal.select();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.SELECTED);
    }

    @Test
    @DisplayName("select() — SUBMITTED가 아닐 때 PROPOSAL_NOT_SELECTABLE 예외")
    void select_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L);

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
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.submit();

        // when
        proposal.markNotSelected();

        // then
        assertThat(proposal.getStatus()).isEqualTo(ProposalStatus.NOT_SELECTED);
    }

    @Test
    @DisplayName("markNotSelected() — SUBMITTED가 아닐 때 PROPOSAL_NOT_SELECTABLE 예외")
    void markNotSelected_잘못된_상태에서_예외() {
        // given
        Proposal proposal = Proposal.create(1L, 10L);

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
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.submit();
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
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.expire();

        // when & then
        assertThat(proposal.isExpired()).isTrue();
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
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now().minusHours(1),
                null
        );

        // when & then
        assertThat(proposal.isExpired()).isTrue();
    }

    @Test
    @DisplayName("isExpired() — SUBMITTED이고 만료시각 전이면 false")
    void isExpired_SUBMITTED이고_만료시각_전이면_false() {
        // given
        Proposal proposal = Proposal.create(1L, 10L);
        proposal.submit();

        // when & then
        assertThat(proposal.isExpired()).isFalse();
    }

    @Test
    @DisplayName("isVisibleToBuyer() — SUBMITTED, EXPIRED, SELECTED, NOT_SELECTED만 true")
    void isVisibleToBuyer_상태별_검증() {
        // given
        Proposal draft = Proposal.create(1L, 10L);
        Proposal submitted = Proposal.create(1L, 11L);
        submitted.submit();
        Proposal selected = Proposal.create(1L, 12L);
        selected.submit();
        selected.select();
        Proposal expired = Proposal.create(1L, 13L);
        expired.expire();

        // then
        assertThat(draft.isVisibleToBuyer()).isFalse();
        assertThat(submitted.isVisibleToBuyer()).isTrue();
        assertThat(selected.isVisibleToBuyer()).isTrue();
        assertThat(expired.isVisibleToBuyer()).isTrue();
    }
}
