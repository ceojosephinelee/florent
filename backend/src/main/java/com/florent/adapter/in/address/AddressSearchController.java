package com.florent.adapter.in.address;

import com.florent.adapter.out.kakao.KakaoAddressSearchAdapter;
import com.florent.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressSearchController {

    private final KakaoAddressSearchAdapter kakaoAddressSearchAdapter;

    public AddressSearchController(KakaoAddressSearchAdapter kakaoAddressSearchAdapter) {
        this.kakaoAddressSearchAdapter = kakaoAddressSearchAdapter;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressSearchResponse>>> search(
            @RequestParam String query) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest()
                    .body((ApiResponse) ApiResponse.error("BAD_REQUEST", "검색어를 입력해주세요."));
        }

        List<AddressSearchResponse> results = kakaoAddressSearchAdapter.search(query);
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
