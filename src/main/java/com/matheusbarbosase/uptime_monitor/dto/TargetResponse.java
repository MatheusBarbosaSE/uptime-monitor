package com.matheusbarbosase.uptime_monitor.dto;

import java.time.Instant;


public record TargetResponse(
        Long id,
        String name,
        String url,
        Instant createdAt,
        Integer checkInterval
) {
}