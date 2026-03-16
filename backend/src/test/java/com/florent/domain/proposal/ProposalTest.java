package com.florent.domain.proposal;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.florent.support.TestFixtures;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProposalTest {

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    private Proposal createSubmittableProposal(Long requestId, Long shopId) {
        Proposal proposal = Proposal.create(requestId, shopId, fixedClock);
        proposal.updateDraft("타이틀", List.of("RED"), List.of("장미"),
                List.of("리본"), null, null, "설명 텍스트",
                null, "PICKUP_30M", "14:00", new BigDecimal("30000"));
        return proposal;
    }

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
    @DisplayName("updateDraft() — DRAFT 상태에서 모든 필드가 업데이트된다")
    void updateDraft_DRAFT_상태에서_모든_필드가_업데이트된다() {
        // given
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // when
        proposal.updateDraft("새 타이틀", List.of("BLUE"), List.of("백합"),
                List.of("포장지"), "알레르기 없음", "관리 팁",
                "새 설명", List.of("https://img.com/1.jpg"),
                "DELIVERY_1H", "15:00", new BigDecimal("50000"));

        // then
        assertThat(proposal.getConceptTitle()).isEqualTo("새 타이틀");
        assertThat(proposal.getMoodColors()).containsExactly("BLUE");
        assertThat(proposal.getMainFlowers()).containsExactly("백합");
        assertThat(proposal.getWrappingStyle()).containsExactly("포장지");
        assertThat(proposal.getAllergyNote()).isEqualTo("알레르기 없음");
        assertThat(proposal.getCareTips()).isEqualTo("관리 팁");
        assertThat(proposal.getDescription()).isEqualTo("새 설명");
        assertThat(proposal.getImageUrls()).containsExactly("https://img.com/1.jpg");
        assertThat(proposal.getAvailableSlotKind()).isEqualTo("DELIVERY_1H");
        assertThat(proposal.getAvailableSlotValue()).isEqualTo("15:00");
        assertThat(proposal.getPrice()).isEqualByComparingTo(new BigDecimal("50000"));
    }

    @Test
    @DisplayName("updateDraft() — DRAFT가 아닌 상태에서 PROPOSAL_NOT_EDITABLE 예외")
    void updateDraft_DRAFT가_아닌_상태에서_PROPOSAL_NOT_EDITABLE_예외() {
        // given
        Proposal proposal = createSubmittableProposal(1L, 10L);
        proposal.submit(fixedClock);

        // when & then
        assertThatThrownBy(() -> proposal.updateDraft("타이틀", List.of(), List.of(),
                List.of(), null, null, "설명", null, "PICKUP_30M", "14:00",
                new BigDecimal("30000")))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_EDITABLE));
    }

    @Test
    @DisplayName("submit() — 만료된 제안 제출 시 PROPOSAL_EXPIRED 예외")
    void submit_만료된_제안_제출_시_PROPOSAL_EXPIRED_예외() {
        // given — reconstitute로 만료된 DRAFT 생성
        Proposal proposal = Proposal.reconstitute(
                1L, 1L, 10L, ProposalStatus.DRAFT,
                "타이틀", List.of("RED"), List.of("장미"),
                List.of("리본"), null, null,
                "설명 텍스트", null, "PICKUP_30M", "14:00",
                new BigDecimal("30000"),
                LocalDateTime.now(fixedClock).minusHours(25),
                LocalDateTime.now(fixedClock).minusHours(1),
                null
        );

        // when & then
        assertThatThrownBy(() -> proposal.submit(fixedClock))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_EXPIRED));
    }

    @Test
    @DisplayName("submit() — DRAFT에서 SUBMITTED로 전이하고 submittedAt이 설정된다")
    void submit_정상_전이() {
        // given
        Proposal proposal = createSubmittableProposal(1L, 10L);

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
        Proposal proposal = createSubmittableProposal(1L, 10L);
        proposal.submit(fixedClock);

        // when & then
        assertThatThrownBy(() -> proposal.submit(fixedClock))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.PROPOSAL_NOT_SUBMITTABLE));
    }

    @Test
    @DisplayName("submit() — 필수 필드 누락 시 VALIDATION_ERROR 예외")
    void submit_필수_필드_누락_시_예외() {
        // given — description 등 미설정
        Proposal proposal = Proposal.create(1L, 10L, fixedClock);

        // when & then
        assertThatThrownBy(() -> proposal.submit(fixedClock))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.VALIDATION_ERROR));
    }

    @Test
    @DisplayName("select() — SUBMITTED에서 SELECTED로 전이한다")
    void select_정상_전이() {
        // given
        Proposal proposal = createSubmittableProposal(1L, 10L);
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
        Proposal proposal = createSubmittableProposal(1L, 10L);
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
        Proposal proposal = createSubmittableProposal(1L, 10L);
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
        Proposal proposal = createSubmittableProposal(1L, 10L);
        proposal.submit(fixedClock);

        // when & then
        assertThat(proposal.isExpired(fixedClock)).isFalse();
    }

    @Test
    @DisplayName("isVisibleToBuyer() — SUBMITTED, EXPIRED, SELECTED, NOT_SELECTED만 true")
    void isVisibleToBuyer_상태별_검증() {
        // given
        Proposal draft = Proposal.create(1L, 10L, fixedClock);
        Proposal submitted = createSubmittableProposal(1L, 11L);
        submitted.submit(fixedClock);
        Proposal selected = createSubmittableProposal(1L, 12L);
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
