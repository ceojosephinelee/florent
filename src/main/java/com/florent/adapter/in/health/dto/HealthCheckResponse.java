package com.florent.adapter.in.health.dto;

public record HealthCheckResponse(
        String status,
        String service
) {
}
