package com.florent.adapter.in.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProvider;
import com.florent.domain.shop.GetShopUseCase;
import com.florent.domain.shop.RegisterShopResult;
import com.florent.domain.shop.RegisterShopUseCase;
import com.florent.domain.shop.ShopDetailResult;
import com.florent.domain.shop.UpdateShopUseCase;
import com.florent.support.WithMockSeller;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SellerShopController.class)
@AutoConfigureMockMvc(addFilters = false)
class SellerShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterShopUseCase registerShopUseCase;

    @MockBean
    private GetShopUseCase getShopUseCase;

    @MockBean
    private UpdateShopUseCase updateShopUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    private static final Long SELLER_ID = 10L;

    // ─── POST /seller/shop ───

    @Test
    @DisplayName("POST /seller/shop — 꽃집 등록 성공 201")
    @WithMockSeller
    void register_성공_201() throws Exception {
        // given
        given(registerShopUseCase.register(eq(SELLER_ID), any()))
                .willReturn(new RegisterShopResult(10L, "플로렌트"));

        // when & then
        mockMvc.perform(post("/api/v1/seller/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "플로렌트",
                                    "description": "예쁜 꽃집",
                                    "phone": "010-1234-5678",
                                    "addressText": "서울시 강남구",
                                    "lat": 37.498095,
                                    "lng": 127.027610
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shopId").value(10))
                .andExpect(jsonPath("$.data.name").value("플로렌트"));
    }

    @Test
    @DisplayName("POST /seller/shop — 중복 등록 시 409")
    @WithMockSeller
    void register_중복_409() throws Exception {
        // given
        given(registerShopUseCase.register(eq(SELLER_ID), any()))
                .willThrow(new BusinessException(ErrorCode.SHOP_ALREADY_EXISTS));

        // when & then
        mockMvc.perform(post("/api/v1/seller/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "플로렌트",
                                    "addressText": "서울시 강남구",
                                    "lat": 37.498095,
                                    "lng": 127.027610
                                }
                                """))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("POST /seller/shop — name 누락 시 400")
    @WithMockSeller
    void register_name_누락_400() throws Exception {
        // when & then
        mockMvc.perform(post("/api/v1/seller/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "addressText": "서울시 강남구",
                                    "lat": 37.498095,
                                    "lng": 127.027610
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /seller/shop ───

    @Test
    @DisplayName("GET /seller/shop — 꽃집 조회 성공 200")
    @WithMockSeller
    void getShop_성공_200() throws Exception {
        // given
        given(getShopUseCase.getShop(SELLER_ID))
                .willReturn(new ShopDetailResult(
                        10L, "플로렌트", "예쁜 꽃집", "010-1234-5678",
                        "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));

        // when & then
        mockMvc.perform(get("/api/v1/seller/shop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shopId").value(10))
                .andExpect(jsonPath("$.data.name").value("플로렌트"))
                .andExpect(jsonPath("$.data.description").value("예쁜 꽃집"))
                .andExpect(jsonPath("$.data.phone").value("010-1234-5678"));
    }

    @Test
    @DisplayName("GET /seller/shop — 미등록 시 404")
    @WithMockSeller
    void getShop_미등록_404() throws Exception {
        // given
        given(getShopUseCase.getShop(SELLER_ID))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/v1/seller/shop"))
                .andExpect(status().isNotFound());
    }

    // ─── PATCH /seller/shop ───

    @Test
    @DisplayName("PATCH /seller/shop — 꽃집 수정 성공 200")
    @WithMockSeller
    void update_성공_200() throws Exception {
        // given
        given(updateShopUseCase.update(eq(SELLER_ID), any()))
                .willReturn(new ShopDetailResult(
                        10L, "새이름", "새설명", "010-9999-9999",
                        "서울시 강남구", new BigDecimal("37.498095"), new BigDecimal("127.027610")));

        // when & then
        mockMvc.perform(patch("/api/v1/seller/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "새이름",
                                    "description": "새설명",
                                    "phone": "010-9999-9999"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("새이름"))
                .andExpect(jsonPath("$.data.description").value("새설명"));
    }

    @Test
    @DisplayName("PATCH /seller/shop — 미등록 시 404")
    @WithMockSeller
    void update_미등록_404() throws Exception {
        // given
        given(updateShopUseCase.update(eq(SELLER_ID), any()))
                .willThrow(new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        // when & then
        mockMvc.perform(patch("/api/v1/seller/shop")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "새이름" }
                                """))
                .andExpect(status().isNotFound());
    }
}
