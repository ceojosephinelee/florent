package com.florent.acceptance.testadapter.http;

import com.florent.acceptance.testadapter.HealthCheckTestAdapter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class HttpHealthCheckTestAdapter implements HealthCheckTestAdapter {

    private final TestRestTemplate testRestTemplate;

    public HttpHealthCheckTestAdapter(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    @Override
    public HealthCheckResult checkHealth() {
        ResponseEntity<HealthResponseDto> response = testRestTemplate.getForEntity(
                "/api/v1/health",
                HealthResponseDto.class
        );

        HealthResponseDto body = response.getBody();
        return new HealthCheckResult(
                response.getStatusCode().value(),
                body == null ? null : body.status(),
                body == null ? null : body.service()
        );
    }

    private record HealthResponseDto(String status, String service) {
    }
}
