package com.florent.adapter.in.buyer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.CreateRequestUseCase;
import com.florent.domain.request.GetRequestDetailUseCase;
import com.florent.domain.request.GetRequestListUseCase;
import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.RequestDetailResult;
import com.florent.domain.request.RequestListResult;
import com.florent.domain.request.RequestStatus;
import com.florent.domain.request.RequestSummaryResult;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.common.security.JwtProvider;
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

@WebMvcTest(controllers = BuyerRequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class BuyerRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateRequestUseCase createRequestUseCase;

    @MockBean
    private GetRequestListUseCase getRequestListUseCase;

    @MockBean
    private GetRequestDetailUseCase getRequestDetailUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("요청 생성 성공 시 201 응답")
    @WithMockBuyer
    void 요청_생성_성공_시_201_응답() throws Exception {
        // given
        LocalDateTime expiresAt = LocalDateTime.of(2026, 3, 17, 10, 0);
        given(createRequestUseCase.create(any()))
                .willReturn(new CreateRequestResult(1L, "OPEN", expiresAt));

        String body = """
                {
                    "purposeTags": ["생일"],
                    "relationTags": ["연인"],
                    "moodTags": ["로맨틱"],
                    "budgetTier": "TIER2",
                    "fulfillmentType": "DELIVERY",
                    "fulfillmentDate": "2026-03-18",
                    "requestedTimeSlots": [{"kind": "DELIVERY_WINDOW", "value": "14:00-16:00"}],
                    "placeAddressText": "서울시 강남구",
                    "placeLat": 37.498095,
                    "placeLng": 127.027610
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/buyer/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    @DisplayName("요청 생성 시 필수 필드 누락 400")
    @WithMockBuyer
    void 요청_생성_시_필수_필드_누락_400() throws Exception {
        // given — purposeTags 누락
        String body = """
                {
                    "relationTags": ["연인"],
                    "moodTags": ["로맨틱"],
                    "budgetTier": "TIER2",
                    "fulfillmentType": "DELIVERY",
                    "fulfillmentDate": "2026-03-18",
                    "requestedTimeSlots": [{"kind": "DELIVERY_WINDOW", "value": "14:00-16:00"}],
                    "placeAddressText": "서울시 강남구",
                    "placeLat": 37.498095,
                    "placeLng": 127.027610
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/buyer/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("요청 목록 조회 성공")
    @WithMockBuyer
    void 요청_목록_조회_성공() throws Exception {
        // given
        RequestSummaryResult summary = new RequestSummaryResult(
                1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"),
                BudgetTier.TIER2, FulfillmentType.DELIVERY,
                LocalDate.of(2026, 3, 18),
                LocalDateTime.of(2026, 3, 17, 10, 0),
                2, 1);
        given(getRequestListUseCase.getList(eq(1L), eq(0), eq(20)))
                .willReturn(new RequestListResult(List.of(summary), 0, 20, 1, 1, true));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/requests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].requestId").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("요청 상세 조회 성공")
    @WithMockBuyer
    void 요청_상세_조회_성공() throws Exception {
        // given
        RequestDetailResult detail = new RequestDetailResult(
                1L, RequestStatus.OPEN,
                List.of("생일"), List.of("연인"), List.of("로맨틱"),
                BudgetTier.TIER2, FulfillmentType.DELIVERY,
                LocalDate.of(2026, 3, 18),
                List.of(new TimeSlot(SlotKind.DELIVERY_WINDOW, "14:00-16:00")),
                "서울시 강남구",
                new BigDecimal("37.498095"), new BigDecimal("127.027610"),
                LocalDateTime.of(2026, 3, 17, 10, 0),
                2, 1);
        given(getRequestDetailUseCase.getDetail(eq(1L), eq(1L))).willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/v1/buyer/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requestId").value(1))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    @DisplayName("존재하지 않는 요청 상세 조회 404")
    @WithMockBuyer
    void 존재하지_않는_요청_상세_조회_404() throws Exception {
        // given
        given(getRequestDetailUseCase.getDetail(eq(999L), eq(1L)))
                .willThrow(new BusinessException(ErrorCode.REQUEST_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/requests/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("REQUEST_NOT_FOUND"));
    }
}
