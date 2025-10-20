package com.matheusbarbosase.uptime_monitor.dto;

import java.time.Instant;


public record HealthCheckResponse(
        Long id,
        Instant checkedAt,
        Integer statusCode,
        String statusMessage
) {
}