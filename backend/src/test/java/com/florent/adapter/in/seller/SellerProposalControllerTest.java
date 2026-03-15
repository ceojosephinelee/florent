package com.florent.adapter.in.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.proposal.GetSellerProposalDetailUseCase;
import com.florent.domain.proposal.GetSellerProposalListUseCase;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.proposal.SaveProposalUseCase;
import com.florent.domain.proposal.StartProposalUseCase;
import com.florent.domain.proposal.SubmitProposalUseCase;
import com.florent.support.WithMockSeller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerProposalController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StartProposalUseCase startProposalUseCase;

    @MockBean
    private SaveProposalUseCase saveProposalUseCase;

    @MockBean
    private SubmitProposalUseCase submitProposalUseCase;

    @MockBean
    private GetSellerProposalListUseCase getSellerProposalListUseCase;

    @MockBean
    private GetSellerProposalDetailUseCase getSellerProposalDetailUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("제안 상세 조회 성공")
    @WithMockSeller
    void 제안_상세_조회_성공() throws Exception {
        // given
        ProposalDetail detail = new ProposalDetail(
                10L, 1L, ProposalStatus.DRAFT,
                100L, "테스트꽃집", "010-1234-5678", "서울시 강남구",
                "봄의 향기",
                List.of("PINK"), List.of("장미"), List.of("리본"),
                "없음", "물을 자주 주세요", "봄 느낌 꽃다발",
                List.of("https://img.example.com/1.jpg"),
                "PICKUP_30M", "14:00",
                LocalDateTime.of(2026, 3, 16, 10, 0),
                new BigDecimal("35000"));
        given(getSellerProposalDetailUseCase.getSellerProposalDetail(eq(10L), eq(10L)))
                .willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/seller/proposals/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.proposalId").value(10))
                .andExpect(jsonPath("$.data.conceptTitle").value("봄의 향기"))
                .andExpect(jsonPath("$.data.price").value(35000));
    }

    @Test
    @DisplayName("존재하지 않는 제안 조회 시 404")
    @WithMockSeller
    void 존재하지_않는_제안_조회_시_404() throws Exception {
        // given
        given(getSellerProposalDetailUseCase.getSellerProposalDetail(eq(999L), eq(10L)))
                .willThrow(new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/proposals/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROPOSAL_NOT_FOUND"));
    }

    @Test
    @DisplayName("타인 제안 조회 시 403")
    @WithMockSeller
    void 타인_제안_조회_시_403() throws Exception {
        // given
        given(getSellerProposalDetailUseCase.getSellerProposalDetail(eq(10L), eq(10L)))
                .willThrow(new BusinessException(ErrorCode.FORBIDDEN));

        // when & then
        mockMvc.perform(get("/api/v1/seller/proposals/10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
