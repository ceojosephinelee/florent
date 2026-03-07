package com.florent.acceptance.testadapter;

public interface HealthCheckTestAdapter {

    HealthCheckResult checkHealth();

    record HealthCheckResult(
            int statusCode,
            String status,
            String service
    ) {
    }
}
