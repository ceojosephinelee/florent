package com.florent.adapter.in.image;

import com.florent.common.security.JwtProvider;
import com.florent.domain.image.GeneratePresignedUrlUseCase;
import com.florent.domain.image.PresignedUrlResult;
import com.florent.support.WithMockAuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GeneratePresignedUrlUseCase generatePresignedUrlUseCase;

    @MockBean
    private JwtProvider jwtProvider;

    @Test
    @DisplayName("presigned URL 발급 성공 시 200 응답")
    @WithMockAuthUser(userId = 1L, buyerId = 1L, role = "BUYER")
    void presigned_URL_발급_성공_시_200_응답() throws Exception {
        // given
        given(generatePresignedUrlUseCase.generate(any()))
                .willReturn(new PresignedUrlResult(
                        "https://s3.amazonaws.com/presigned/flower.jpg",
                        "https://s3.amazonaws.com/images/flower.jpg"
                ));

        String body = """
                {
                  "fileName": "flower.jpg",
                  "contentType": "image/jpeg",
                  "target": "PROPOSAL"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/images/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.presignedUrl").value("https://s3.amazonaws.com/presigned/flower.jpg"))
                .andExpect(jsonPath("$.data.imageUrl").value("https://s3.amazonaws.com/images/flower.jpg"));
    }

    @Test
    @DisplayName("fileName 누락 시 400 응답")
    @WithMockAuthUser(userId = 1L, buyerId = 1L, role = "BUYER")
    void fileName_누락_시_400_응답() throws Exception {
        // given
        String body = """
                {
                  "contentType": "image/jpeg",
                  "target": "PROPOSAL"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/images/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("target 누락 시 400 응답")
    @WithMockAuthUser(userId = 1L, buyerId = 1L, role = "BUYER")
    void target_누락_시_400_응답() throws Exception {
        // given
        String body = """
                {
                  "fileName": "flower.jpg",
                  "contentType": "image/jpeg"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/images/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
