package com.florent.adapter.in.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.GetSellerRequestDetailUseCase;
import com.florent.domain.request.GetSellerRequestListUseCase;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.request.SellerRequestDetailResult;
import com.florent.domain.request.SellerRequestListResult;
import com.florent.domain.request.SellerRequestSummaryResult;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.support.WithMockSeller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSellerRequestListUseCase getSellerRequestListUseCase;

    @MockBean
    private GetSellerRequestDetailUseCase getSellerRequestDetailUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    private static final Long SELLER_ID = 10L;

    // ─── GET /seller/requests ───

    @Test
    @DisplayName("GET /seller/requests — 목록 조회 성공 200")
    @WithMockSeller
    void 목록_조회_성공_200() throws Exception {
        // given
        SellerRequestSummaryResult summary = new SellerRequestSummaryResult(
                1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"), List.of("따뜻한"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.of(2026, 3, 20),
                LocalDateTime.of(2026, 3, 17, 19, 0),
                "DRAFT");

        SellerRequestListResult listResult = new SellerRequestListResult(
                List.of(summary), 0, 20, 1, 1, true);

        given(getSellerRequestListUseCase.getSellerRequests(eq(SELLER_ID), anyInt(), anyInt()))
                .willReturn(listResult);

        // when & then
        mockMvc.perform(get("/api/v1/seller/requests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].requestId").value(1))
                .andExpect(jsonPath("$.data.content[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data.content[0].myProposalStatus").value("DRAFT"))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /seller/requests — 꽃집 미등록 시 404")
    @WithMockSeller
    void 목록_꽃집_미등록_404() throws Exception {
        // given
        given(getSellerRequestListUseCase.getSellerRequests(eq(SELLER_ID), anyInt(), anyInt()))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/requests"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /seller/requests/{requestId} ───

    @Test
    @DisplayName("GET /seller/requests/{requestId} — 상세 조회 성공 200")
    @WithMockSeller
    void 상세_조회_성공_200() throws Exception {
        // given
        SellerRequestDetailResult detail = new SellerRequestDetailResult(
                1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"), List.of("따뜻한"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.of(2026, 3, 20),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구",
                LocalDateTime.of(2026, 3, 17, 19, 0),
                5L, "DRAFT");

        given(getSellerRequestDetailUseCase.getSellerRequestDetail(eq(1L), eq(SELLER_ID)))
                .willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/seller/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.requestedTimeSlots[0].kind").value("PICKUP_30M"))
                .andExpect(jsonPath("$.data.requestedTimeSlots[0].value").value("14:00"))
                .andExpect(jsonPath("$.data.placeAddressText").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.myProposalId").value(5))
                .andExpect(jsonPath("$.data.myProposalStatus").value("DRAFT"));
    }

    @Test
    @DisplayName("GET /seller/requests/{requestId} — 존재하지 않는 요청 404")
    @WithMockSeller
    void 상세_존재하지_않는_요청_404() throws Exception {
        // given
        given(getSellerRequestDetailUseCase.getSellerRequestDetail(eq(999L), eq(SELLER_ID)))
                .willThrow(new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/requests/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /seller/requests/{requestId} — myProposalId null일 때 정상")
    @WithMockSeller
    void 상세_myProposalId_null() throws Exception {
        // given
        SellerRequestDetailResult detail = new SellerRequestDetailResult(
                1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"), List.of("따뜻한"),
                BudgetTier.TIER2, FulfillmentType.PICKUP,
                LocalDate.of(2026, 3, 20),
                List.of(new TimeSlot(SlotKind.PICKUP_30M, "14:00")),
                "서울시 강남구",
                LocalDateTime.of(2026, 3, 17, 19, 0),
                null, null);

        given(getSellerRequestDetailUseCase.getSellerRequestDetail(eq(1L), eq(SELLER_ID)))
                .willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/seller/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.myProposalId").doesNotExist())
                .andExpect(jsonPath("$.data.myProposalStatus").doesNotExist());
    }
}
