package com.florent.adapter.out.kakao;

import com.florent.adapter.in.address.AddressSearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Component
public class KakaoAddressSearchAdapter {

    private static final Logger log = LoggerFactory.getLogger(KakaoAddressSearchAdapter.class);
    private static final int MAX_SIZE = 5;

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String restKey;

    public KakaoAddressSearchAdapter(
            RestTemplate restTemplate,
            @Value("${kakao.local.base-url}") String baseUrl,
            @Value("${kakao.rest-key}") String restKey) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.restKey = restKey;
    }

    private static final java.util.regex.Pattern DONG_PATTERN =
            java.util.regex.Pattern.compile(".*[동洞]\\d*$");

    public List<AddressSearchResponse> search(String query) {
        log.debug("주소 검색 요청: query={}", query);

        // 1) 동 이름 패턴이면 행정구역 API로 중심 좌표 조회
        if (DONG_PATTERN.matcher(query.trim()).matches()) {
            log.debug("동 패턴 감지, 행정구역 API 우선 호출: query={}", query);
            List<AddressSearchResponse> regionResults = callRegionCodeApi(query);
            if (!regionResults.isEmpty()) {
                log.debug("행정구역 API 결과: query={}, count={}", query, regionResults.size());
                return regionResults;
            }
            log.debug("행정구역 API 결과 없음, 주소 검색 폴백: query={}", query);
        }

        // 2) 주소 검색 (도로명/지번)
        List<AddressSearchResponse> results = callAddressApi(query);

        // 3) 결과 없으면 키워드 검색 (장소명/역명 등) 폴백
        if (results.isEmpty()) {
            log.debug("주소 검색 결과 없음, 키워드 검색 폴백: query={}", query);
            results = callKeywordApi(query);
        }

        log.debug("주소 검색 결과: query={}, count={}", query, results.size());
        return results;
    }

    private List<AddressSearchResponse> callAddressApi(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v2/local/search/address.json")
                .queryParam("query", query)
                .queryParam("size", MAX_SIZE)
                .build()
                .encode()
                .toUri();

        Map<String, Object> body = callKakaoApi(uri);
        return toAddressResults(body);
    }

    private List<AddressSearchResponse> callRegionCodeApi(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v2/local/search/address.json")
                .queryParam("query", query)
                .queryParam("size", MAX_SIZE)
                .queryParam("analyze_type", "similar")
                .build()
                .encode()
                .toUri();

        Map<String, Object> body = callKakaoApi(uri);
        return toRegionResults(body);
    }

    @SuppressWarnings("unchecked")
    private List<AddressSearchResponse> toRegionResults(Map<String, Object> body) {
        if (body == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }
        // 동 검색 시 address.h_code(행정동 코드)가 있는 결과만 필터링하고
        // address의 x, y를 동 중심 좌표로 사용
        return documents.stream()
                .filter(doc -> {
                    Map<String, Object> address = (Map<String, Object>) doc.get("address");
                    return address != null && address.get("h_code") != null
                            && !address.get("h_code").toString().isEmpty();
                })
                .map(doc -> {
                    Map<String, Object> address = (Map<String, Object>) doc.get("address");
                    String addressName = (String) address.get("address_name");
                    double lat = parseDouble(address.get("y"));
                    double lng = parseDouble(address.get("x"));
                    return new AddressSearchResponse(addressName, lat, lng);
                })
                .limit(MAX_SIZE)
                .toList();
    }

    private List<AddressSearchResponse> callKeywordApi(String query) {
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/v2/local/search/keyword.json")
                .queryParam("query", query)
                .queryParam("size", MAX_SIZE)
                .build()
                .encode()
                .toUri();

        Map<String, Object> body = callKakaoApi(uri);
        return toKeywordResults(body);
    }

    private Map<String, Object> callKakaoApi(URI uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "KakaoAK " + restKey);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {});

            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("카카오 API 클라이언트 오류: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ResponseStatusException(BAD_GATEWAY, "카카오 API 호출 실패");
        } catch (HttpServerErrorException e) {
            log.error("카카오 API 서버 오류: status={}", e.getStatusCode());
            throw new ResponseStatusException(BAD_GATEWAY, "카카오 API 서버 오류");
        } catch (ResourceAccessException e) {
            log.error("카카오 API 타임아웃", e);
            throw new ResponseStatusException(SERVICE_UNAVAILABLE, "카카오 API 응답 시간 초과");
        } catch (Exception e) {
            log.error("카카오 API 호출 중 예상치 못한 오류", e);
            throw new ResponseStatusException(BAD_GATEWAY, "카카오 API 호출 실패");
        }
    }

    // --- address API 응답 파싱 ---

    @SuppressWarnings("unchecked")
    private List<AddressSearchResponse> toAddressResults(Map<String, Object> body) {
        if (body == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }
        return documents.stream()
                .map(this::parseAddressDoc)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private AddressSearchResponse parseAddressDoc(Map<String, Object> doc) {
        String addressName = (String) doc.get("address_name");
        double lat = parseDouble(doc.get("y"));
        double lng = parseDouble(doc.get("x"));

        Map<String, Object> roadAddress = (Map<String, Object>) doc.get("road_address");
        Map<String, Object> address = (Map<String, Object>) doc.get("address");

        if (roadAddress != null && roadAddress.get("address_name") != null) {
            addressName = (String) roadAddress.get("address_name");
        } else if (address != null && address.get("address_name") != null) {
            addressName = (String) address.get("address_name");
        }

        return new AddressSearchResponse(addressName, lat, lng);
    }

    // --- keyword API 응답 파싱 ---

    @SuppressWarnings("unchecked")
    private List<AddressSearchResponse> toKeywordResults(Map<String, Object> body) {
        if (body == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
        if (documents == null || documents.isEmpty()) {
            return Collections.emptyList();
        }
        return documents.stream()
                .map(this::parseKeywordDoc)
                .toList();
    }

    private AddressSearchResponse parseKeywordDoc(Map<String, Object> doc) {
        // keyword API는 road_address_name, address_name, place_name 필드 구조가 다름
        String placeName = (String) doc.get("place_name");
        String roadAddressName = (String) doc.get("road_address_name");
        String addressName = (String) doc.get("address_name");
        double lat = parseDouble(doc.get("y"));
        double lng = parseDouble(doc.get("x"));

        // 도로명 주소 우선, 없으면 지번, 장소명은 표시용으로 앞에 붙임
        String displayAddress;
        if (roadAddressName != null && !roadAddressName.isEmpty()) {
            displayAddress = roadAddressName;
        } else if (addressName != null && !addressName.isEmpty()) {
            displayAddress = addressName;
        } else {
            displayAddress = placeName;
        }

        return new AddressSearchResponse(displayAddress, lat, lng);
    }

    private double parseDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
