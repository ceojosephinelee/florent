package com.florent.adapter.in.address;

import com.florent.adapter.out.kakao.KakaoAddressSearchAdapter;
import com.florent.common.security.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AddressSearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KakaoAddressSearchAdapter kakaoAddressSearchAdapter;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("주소 검색 정상 요청 시 200과 결과 목록을 반환한다")
    void 주소_검색_정상_요청() throws Exception {
        // given
        String query = "강남대로";
        List<AddressSearchResponse> results = List.of(
                new AddressSearchResponse("서울 강남구 강남대로 396", 37.497625, 127.028399),
                new AddressSearchResponse("서울 강남구 강남대로 100", 37.501234, 127.025678)
        );
        given(kakaoAddressSearchAdapter.search(query)).willReturn(results);

        // when & then
        mockMvc.perform(get("/api/v1/addresses/search")
                        .param("query", query))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].addressName").value("서울 강남구 강남대로 396"))
                .andExpect(jsonPath("$.data[0].lat").value(37.497625))
                .andExpect(jsonPath("$.data[0].lng").value(127.028399));
    }

    @Test
    @DisplayName("검색어가 빈 문자열이면 400을 반환한다")
    void 검색어_빈문자열_400() throws Exception {
        // when & then
        mockMvc.perform(get("/api/v1/addresses/search")
                        .param("query", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }
}
