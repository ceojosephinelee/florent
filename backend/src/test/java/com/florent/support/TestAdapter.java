package com.florent.support;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestAdapter {

    private final TestRestTemplate restTemplate;

    public TestAdapter(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> getHealth() {
        return restTemplate.getForEntity("/actuator/health", String.class);
    }

    public ResponseEntity<String> createRequest(String token, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        return restTemplate.exchange(
                "/api/v1/buyer/requests",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);
    }

    public ResponseEntity<String> createRequestWithoutAuth(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.exchange(
                "/api/v1/buyer/requests",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class);
    }

    public ResponseEntity<String> getRequestList(String token, int page, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return restTemplate.exchange(
                "/api/v1/buyer/requests?page=" + page + "&size=" + size,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
    }

    public ResponseEntity<String> getRequestDetail(String token, Long requestId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return restTemplate.exchange(
                "/api/v1/buyer/requests/" + requestId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
    }
}
