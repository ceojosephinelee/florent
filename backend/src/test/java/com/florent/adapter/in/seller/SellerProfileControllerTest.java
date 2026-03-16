package com.florent.adapter.in.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.seller.GetSellerHomeUseCase;
import com.florent.domain.seller.GetSellerProfileUseCase;
import com.florent.domain.seller.GetSellerStatsUseCase;
import com.florent.domain.seller.SellerHomeResult;
import com.florent.domain.seller.SellerProfileResult;
import com.florent.domain.seller.SellerStatsResult;
import com.florent.support.WithMockSeller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerProfileControllerTest {

    private static final Long SELLER_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSellerProfileUseCase getSellerProfileUseCase;

    @MockBean
    private GetSellerHomeUseCase getSellerHomeUseCase;

    @MockBean
    private GetSellerStatsUseCase getSellerStatsUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    // ─── GET /seller/me ───

    @Test
    @DisplayName("GET /seller/me — 프로필 조회 성공 200")
    @WithMockSeller
    void 프로필_조회_성공_200() throws Exception {
        // given
        given(getSellerProfileUseCase.getProfile(SELLER_ID))
                .willReturn(new SellerProfileResult(
                        SELLER_ID, "플로렌트", "서울시 강남구", "SELLER",
                        LocalDateTime.of(2025, 5, 1, 10, 0)));

        // when & then
        mockMvc.perform(get("/api/v1/seller/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sellerId").value(SELLER_ID))
                .andExpect(jsonPath("$.data.shopName").value("플로렌트"))
                .andExpect(jsonPath("$.data.shopAddress").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.role").value("SELLER"));
    }

    @Test
    @DisplayName("GET /seller/me — 꽃집 미등록 시 404")
    @WithMockSeller
    void 꽃집_미등록_404() throws Exception {
        // given
        given(getSellerProfileUseCase.getProfile(SELLER_ID))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/me"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /seller/home ───

    @Test
    @DisplayName("GET /seller/home — 홈 대시보드 조회 성공 200")
    @WithMockSeller
    void 홈_대시보드_200() throws Exception {
        // given
        SellerHomeResult.RecentRequestItem recentItem = new SellerHomeResult.RecentRequestItem(
                1L, "OPEN", List.of("생일"), "TIER2", "PICKUP",
                LocalDate.of(2025, 6, 1),
                LocalDateTime.of(2025, 6, 3, 14, 0));

        given(getSellerHomeUseCase.getHome(SELLER_ID))
                .willReturn(new SellerHomeResult(5, 1, 3, 2, List.of(recentItem)));

        // when & then
        mockMvc.perform(get("/api/v1/seller/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.openRequestCount").value(5))
                .andExpect(jsonPath("$.data.draftProposalCount").value(1))
                .andExpect(jsonPath("$.data.submittedProposalCount").value(3))
                .andExpect(jsonPath("$.data.confirmedReservationCount").value(2))
                .andExpect(jsonPath("$.data.recentRequests[0].requestId").value(1))
                .andExpect(jsonPath("$.data.recentRequests[0].status").value("OPEN"));
    }

    @Test
    @DisplayName("GET /seller/home — 꽃집 미등록 시 404")
    @WithMockSeller
    void 홈_꽃집_미등록_404() throws Exception {
        // given
        given(getSellerHomeUseCase.getHome(SELLER_ID))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/home"))
                .andExpect(status().isNotFound());
    }

    // ─── GET /seller/stats ───

    @Test
    @DisplayName("GET /seller/stats — 통계 조회 성공 200")
    @WithMockSeller
    void 통계_조회_200() throws Exception {
        // given
        SellerStatsResult.RecentReservationItem recentItem = new SellerStatsResult.RecentReservationItem(
                1L, "봄 부케", new BigDecimal("35000"), "PICKUP",
                LocalDateTime.of(2025, 5, 30, 10, 0));

        given(getSellerStatsUseCase.getStats(SELLER_ID))
                .willReturn(new SellerStatsResult(12, 8, 5, List.of(recentItem)));

        // when & then
        mockMvc.perform(get("/api/v1/seller/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.monthlyReceivedRequestCount").value(12))
                .andExpect(jsonPath("$.data.monthlySubmittedProposalCount").value(8))
                .andExpect(jsonPath("$.data.monthlyConfirmedReservationCount").value(5))
                .andExpect(jsonPath("$.data.recentReservations[0].reservationId").value(1))
                .andExpect(jsonPath("$.data.recentReservations[0].conceptTitle").value("봄 부케"))
                .andExpect(jsonPath("$.data.recentReservations[0].price").value(35000));
    }

    @Test
    @DisplayName("GET /seller/stats — 꽃집 미등록 시 404")
    @WithMockSeller
    void 통계_꽃집_미등록_404() throws Exception {
        // given
        given(getSellerStatsUseCase.getStats(SELLER_ID))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/stats"))
                .andExpect(status().isNotFound());
    }
}
