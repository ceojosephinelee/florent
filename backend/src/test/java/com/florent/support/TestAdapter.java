package com.florent.support;

import org.springframework.boot.test.web.client.TestRestTemplate;
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
}