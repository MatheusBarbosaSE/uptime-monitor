package com.matheusbarbosase.uptime_monitor.controller;

import com.matheusbarbosase.uptime_monitor.dto.HealthCheckResponse;
import com.matheusbarbosase.uptime_monitor.service.HealthCheckService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/targets/{targetId}/health-checks")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;

    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }

    @GetMapping
    public Page<HealthCheckResponse> getHealthChecksForTarget(@PathVariable Long targetId, Pageable pageable) {
        return healthCheckService.findChecksByTargetId(targetId, pageable);
    }
}