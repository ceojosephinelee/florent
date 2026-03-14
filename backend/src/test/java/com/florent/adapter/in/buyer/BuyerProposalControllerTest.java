package com.florent.adapter.in.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.proposal.GetProposalDetailUseCase;
import com.florent.domain.proposal.GetProposalListUseCase;
import com.florent.domain.proposal.ProposalDetail;
import com.florent.domain.proposal.ProposalStatus;
import com.florent.domain.proposal.ProposalSummary;
import com.florent.support.WithMockBuyer;
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

@WebMvcTest(controllers = BuyerProposalController.class)
@AutoConfigureMockMvc(addFilters = false)
class BuyerProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetProposalListUseCase getProposalListUseCase;

    @MockBean
    private GetProposalDetailUseCase getProposalDetailUseCase;

    @Test
    @DisplayName("제안 목록 조회 성공")
    @WithMockBuyer
    void 제안_목록_조회_성공() throws Exception {
        // given
        List<ProposalSummary> summaries = List.of(
                new ProposalSummary(10L, "꽃집A", "로맨틱 부케",
                        ProposalStatus.SUBMITTED, LocalDateTime.of(2026, 3, 16, 10, 0)));
        given(getProposalListUseCase.getProposalsByRequestId(eq(1L), eq(1L)))
                .willReturn(summaries);

        // when & then
        mockMvc.perform(get("/api/v1/buyer/requests/1/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].proposalId").value(10))
                .andExpect(jsonPath("$.data[0].shopName").value("꽃집A"));
    }

    @Test
    @DisplayName("제안 상세 조회 성공")
    @WithMockBuyer
    void 제안_상세_조회_성공() throws Exception {
        // given
        ProposalDetail detail = new ProposalDetail(
                10L, 1L, ProposalStatus.SUBMITTED,
                100L, "꽃집A", "010-1234-5678", "서울시 강남구",
                "로맨틱 부케",
                List.of("핑크"), List.of("장미"), List.of("라운드"),
                "없음", "물 자주 주세요", "아름다운 부케",
                List.of("https://img.example.com/1.jpg"),
                "DELIVERY_WINDOW", "14:00-16:00",
                LocalDateTime.of(2026, 3, 16, 10, 0),
                new BigDecimal("50000"));
        given(getProposalDetailUseCase.getProposalDetail(eq(10L), eq(1L)))
                .willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/buyer/proposals/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.proposalId").value(10))
                .andExpect(jsonPath("$.data.shop.name").value("꽃집A"))
                .andExpect(jsonPath("$.data.price").value(50000));
    }

    @Test
    @DisplayName("존재하지 않는 제안 상세 조회 404")
    @WithMockBuyer
    void 존재하지_않는_제안_상세_조회_404() throws Exception {
        // given
        given(getProposalDetailUseCase.getProposalDetail(eq(999L), eq(1L)))
                .willThrow(new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/proposals/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROPOSAL_NOT_FOUND"));
    }

    @Test
    @DisplayName("타인의 요청에 대한 제안 목록 조회 시 403")
    @WithMockBuyer
    void 타인의_요청에_대한_제안_목록_조회_시_403() throws Exception {
        // given
        given(getProposalListUseCase.getProposalsByRequestId(eq(1L), eq(1L)))
                .willThrow(new BusinessException(ErrorCode.FORBIDDEN));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/requests/1/proposals"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
