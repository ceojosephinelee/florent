package com.florent.adapter.in.buyer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.reservation.BuyerReservationDetailResult;
import com.florent.domain.reservation.BuyerReservationSummaryResult;
import com.florent.domain.reservation.ConfirmReservationCommand;
import com.florent.domain.reservation.ConfirmReservationResult;
import com.florent.domain.reservation.ConfirmReservationUseCase;
import com.florent.domain.reservation.GetBuyerReservationDetailUseCase;
import com.florent.domain.reservation.GetBuyerReservationListUseCase;
import com.florent.support.WithMockBuyer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuyerReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class BuyerReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfirmReservationUseCase confirmReservationUseCase;

    @MockBean
    private GetBuyerReservationListUseCase getBuyerReservationListUseCase;

    @MockBean
    private GetBuyerReservationDetailUseCase getBuyerReservationDetailUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // ─── confirm ───

    @Test
    @DisplayName("예약 확정 성공 201")
    @WithMockBuyer
    void 예약_확정_성공_201() throws Exception {
        // given
        ConfirmReservationResult result = new ConfirmReservationResult(
                1L, "CONFIRMED", "SUCCEEDED", new BigDecimal("35000"));
        given(confirmReservationUseCase.confirm(any(ConfirmReservationCommand.class)))
                .willReturn(result);

        String body = MAPPER.writeValueAsString(
                java.util.Map.of("idempotencyKey", "uuid-key-123"));

        // when & then
        mockMvc.perform(post("/api/v1/buyer/proposals/10/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reservationId").value(1))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.paymentStatus").value("SUCCEEDED"))
                .andExpect(jsonPath("$.data.amount").value(35000));
    }

    @Test
    @DisplayName("idempotencyKey 없이 예약 확정 시 400")
    @WithMockBuyer
    void 예약_확정_idempotencyKey_없으면_400() throws Exception {
        // given
        String body = "{}";

        // when & then
        mockMvc.perform(post("/api/v1/buyer/proposals/10/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 제안 선택 시 404")
    @WithMockBuyer
    void 예약_확정_존재하지_않는_제안_404() throws Exception {
        // given
        given(confirmReservationUseCase.confirm(any(ConfirmReservationCommand.class)))
                .willThrow(new BusinessException(ErrorCode.PROPOSAL_NOT_FOUND));

        String body = MAPPER.writeValueAsString(
                java.util.Map.of("idempotencyKey", "uuid-key-123"));

        // when & then
        mockMvc.perform(post("/api/v1/buyer/proposals/999/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("PROPOSAL_NOT_FOUND"));
    }

    @Test
    @DisplayName("중복 결제 시 422")
    @WithMockBuyer
    void 예약_확정_중복_결제_422() throws Exception {
        // given
        given(confirmReservationUseCase.confirm(any(ConfirmReservationCommand.class)))
                .willThrow(new BusinessException(ErrorCode.DUPLICATE_PAYMENT));

        String body = MAPPER.writeValueAsString(
                java.util.Map.of("idempotencyKey", "uuid-key-123"));

        // when & then
        mockMvc.perform(post("/api/v1/buyer/proposals/10/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_PAYMENT"));
    }

    @Test
    @DisplayName("이미 확정된 요청의 제안 선택 시 422")
    @WithMockBuyer
    void 예약_확정_이미_확정된_요청_422() throws Exception {
        // given
        given(confirmReservationUseCase.confirm(any(ConfirmReservationCommand.class)))
                .willThrow(new BusinessException(ErrorCode.REQUEST_ALREADY_CONFIRMED));

        String body = MAPPER.writeValueAsString(
                java.util.Map.of("idempotencyKey", "uuid-key-123"));

        // when & then
        mockMvc.perform(post("/api/v1/buyer/proposals/10/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("REQUEST_ALREADY_CONFIRMED"));
    }

    // ─── getList ───

    @Test
    @DisplayName("예약 목록 조회 성공")
    @WithMockBuyer
    void 예약_목록_조회_성공() throws Exception {
        // given
        List<BuyerReservationSummaryResult> results = List.of(
                new BuyerReservationSummaryResult(
                        1L, "CONFIRMED", "테스트꽃집", "봄의 향기",
                        new BigDecimal("35000"), "PICKUP",
                        LocalDate.of(2026, 3, 20),
                        "PICKUP_30M", "14:00",
                        LocalDateTime.of(2026, 3, 15, 10, 0)));
        given(getBuyerReservationListUseCase.getList(eq(1L)))
                .willReturn(results);

        // when & then
        mockMvc.perform(get("/api/v1/buyer/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].reservationId").value(1))
                .andExpect(jsonPath("$.data[0].shopName").value("테스트꽃집"))
                .andExpect(jsonPath("$.data[0].conceptTitle").value("봄의 향기"))
                .andExpect(jsonPath("$.data[0].price").value(35000))
                .andExpect(jsonPath("$.data[0].fulfillmentSlot.kind").value("PICKUP_30M"))
                .andExpect(jsonPath("$.data[0].fulfillmentSlot.value").value("14:00"));
    }

    @Test
    @DisplayName("예약 목록 빈 배열 반환")
    @WithMockBuyer
    void 예약_목록_빈_배열_반환() throws Exception {
        // given
        given(getBuyerReservationListUseCase.getList(eq(1L)))
                .willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/v1/buyer/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ─── getDetail ───

    @Test
    @DisplayName("예약 상세 조회 성공")
    @WithMockBuyer
    void 예약_상세_조회_성공() throws Exception {
        // given
        BuyerReservationDetailResult result = new BuyerReservationDetailResult(
                1L, "CONFIRMED", "PICKUP",
                LocalDate.of(2026, 3, 20),
                "PICKUP_30M", "14:00",
                "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                LocalDateTime.of(2026, 3, 15, 10, 0),
                new BuyerReservationDetailResult.ProposalInfo(
                        10L, "봄의 향기", "봄 느낌 꽃다발",
                        List.of("https://img.com/1.jpg"), new BigDecimal("35000")),
                new BuyerReservationDetailResult.ShopInfo(
                        100L, "테스트꽃집", "010-1234-5678",
                        "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")),
                new BuyerReservationDetailResult.RequestInfo(
                        1L, List.of("생일"), List.of("연인"),
                        List.of("로맨틱"), "TIER2"));
        given(getBuyerReservationDetailUseCase.getDetail(eq(1L), eq(1L)))
                .willReturn(result);

        // when & then
        mockMvc.perform(get("/api/v1/buyer/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reservationId").value(1))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.proposal.conceptTitle").value("봄의 향기"))
                .andExpect(jsonPath("$.data.shop.name").value("테스트꽃집"))
                .andExpect(jsonPath("$.data.request.budgetTier").value("TIER2"))
                .andExpect(jsonPath("$.data.fulfillmentSlot.kind").value("PICKUP_30M"));
    }

    @Test
    @DisplayName("존재하지 않는 예약 상세 조회 404")
    @WithMockBuyer
    void 존재하지_않는_예약_상세_조회_404() throws Exception {
        // given
        given(getBuyerReservationDetailUseCase.getDetail(eq(999L), eq(1L)))
                .willThrow(new BusinessException(ErrorCode.RESERVATION_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/reservations/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("RESERVATION_NOT_FOUND"));
    }

    @Test
    @DisplayName("타인의 예약 상세 조회 403")
    @WithMockBuyer
    void 타인의_예약_상세_조회_403() throws Exception {
        // given
        given(getBuyerReservationDetailUseCase.getDetail(eq(1L), eq(1L)))
                .willThrow(new BusinessException(ErrorCode.FORBIDDEN));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/reservations/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
