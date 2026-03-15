package com.florent.adapter.in.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.reservation.GetSellerReservationDetailUseCase;
import com.florent.domain.reservation.GetSellerReservationListUseCase;
import com.florent.domain.reservation.SellerReservationDetailResult;
import com.florent.domain.reservation.SellerReservationSummaryResult;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetSellerReservationListUseCase getSellerReservationListUseCase;

    @MockBean
    private GetSellerReservationDetailUseCase getSellerReservationDetailUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    // ─── getList ───

    @Test
    @DisplayName("판매자 예약 목록 조회 성공")
    @WithMockSeller
    void 판매자_예약_목록_조회_성공() throws Exception {
        // given
        List<SellerReservationSummaryResult> results = List.of(
                new SellerReservationSummaryResult(
                        1L, "CONFIRMED", "봄의 향기",
                        new BigDecimal("35000"), "PICKUP",
                        LocalDate.of(2026, 3, 20),
                        "PICKUP_30M", "14:00",
                        "구매자닉네임",
                        LocalDateTime.of(2026, 3, 15, 10, 0)));
        given(getSellerReservationListUseCase.getList(eq(10L)))
                .willReturn(results);

        // when & then
        mockMvc.perform(get("/api/v1/seller/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].reservationId").value(1))
                .andExpect(jsonPath("$.data[0].conceptTitle").value("봄의 향기"))
                .andExpect(jsonPath("$.data[0].price").value(35000))
                .andExpect(jsonPath("$.data[0].buyerNickName").value("구매자닉네임"))
                .andExpect(jsonPath("$.data[0].fulfillmentSlot.kind").value("PICKUP_30M"))
                .andExpect(jsonPath("$.data[0].fulfillmentSlot.value").value("14:00"));
    }

    @Test
    @DisplayName("판매자 예약 목록 빈 배열 반환")
    @WithMockSeller
    void 판매자_예약_목록_빈_배열_반환() throws Exception {
        // given
        given(getSellerReservationListUseCase.getList(eq(10L)))
                .willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/seller/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── getDetail ───

    @Test
    @DisplayName("판매자 예약 상세 조회 성공")
    @WithMockSeller
    void 판매자_예약_상세_조회_성공() throws Exception {
        // given
        SellerReservationDetailResult result = new SellerReservationDetailResult(
                1L, "CONFIRMED", "PICKUP",
                LocalDate.of(2026, 3, 20),
                "PICKUP_30M", "14:00",
                "구매자닉네임",
                "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                LocalDateTime.of(2026, 3, 15, 10, 0),
                new SellerReservationDetailResult.ProposalInfo(
                        10L, "봄의 향기", "봄 느낌 꽃다발",
                        List.of("https://img.com/1.jpg"), new BigDecimal("35000")),
                new SellerReservationDetailResult.RequestInfo(
                        1L, List.of("생일"), List.of("연인"),
                        List.of("로맨틱"), "TIER2"));
        given(getSellerReservationDetailUseCase.getDetail(eq(1L), eq(10L)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/seller/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reservationId").value(1))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.buyerNickName").value("구매자닉네임"))
                .andExpect(jsonPath("$.data.proposal.conceptTitle").value("봄의 향기"))
                .andExpect(jsonPath("$.data.request.budgetTier").value("TIER2"))
                .andExpect(jsonPath("$.data.placeAddressText").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.fulfillmentSlot.kind").value("PICKUP_30M"));
    }

    @Test
    @DisplayName("존재하지 않는 예약 상세 조회 404")
    @WithMockSeller
    void 존재하지_않는_예약_상세_조회_404() throws Exception {
        // given
        given(getSellerReservationDetailUseCase.getDetail(eq(999L), eq(10L)))
                .willThrow(new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/reservations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESERVATION_NOT_FOUND"));
    }

    @Test
    @DisplayName("타인의 예약 상세 조회 403")
    @WithMockSeller
    void 타인의_예약_상세_조회_403() throws Exception {
        // given
        given(getSellerReservationDetailUseCase.getDetail(eq(1L), eq(10L)))
                .willThrow(new BusinessException(ErrorCode.FORBIDDEN));

        // when & then
        mockMvc.perform(get("/api/v1/seller/reservations/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
