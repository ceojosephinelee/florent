package com.florent.adapter.in.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.buyer.BuyerProfileResult;
import com.florent.domain.buyer.GetBuyerProfileUseCase;
import com.florent.support.WithMockBuyer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BuyerProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class BuyerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetBuyerProfileUseCase getBuyerProfileUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("GET /buyer/me — 프로필 조회 성공 200")
    @WithMockBuyer
    void 프로필_조회_성공_200() throws Exception {
        // given
        given(getBuyerProfileUseCase.getProfile(1L, 1L))
                .willReturn(new BuyerProfileResult(
                        1L, "구매자닉네임", "buyer@test.com", "BUYER",
                        LocalDateTime.of(2025, 5, 1, 10, 0)));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.buyerId").value(1))
                .andExpect(jsonPath("$.data.nickName").value("구매자닉네임"))
                .andExpect(jsonPath("$.data.email").value("buyer@test.com"))
                .andExpect(jsonPath("$.data.role").value("BUYER"));
    }

    @Test
    @DisplayName("GET /buyer/me — 존재하지 않는 구매자 404")
    @WithMockBuyer
    void 존재하지_않는_구매자_404() throws Exception {
        // given
        given(getBuyerProfileUseCase.getProfile(1L, 1L))
                .willThrow(new BusinessException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/buyer/me"))
                .andExpect(status().isNotFound());
    }
}
