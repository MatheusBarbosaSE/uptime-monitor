package com.matheusbarbosase.uptime_monitor.controller;

import com.matheusbarbosase.uptime_monitor.dto.HealthCheckResponse;
import com.matheusbarbosase.uptime_monitor.service.HealthCheckService;
import com.matheusbarbosase.uptime_monitor.service.TargetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;


@RestController
@RequestMapping("/api/targets/{targetId}/health-checks")
public class HealthCheckController {

    private final HealthCheckService healthCheckService;
    private final TargetService targetService;

    public HealthCheckController(HealthCheckService healthCheckService, TargetService targetService) {
        this.healthCheckService = healthCheckService;
        this.targetService = targetService;
    }

    @GetMapping
    public Page<HealthCheckResponse> getHealthChecksForTarget(
            @PathVariable Long targetId,

            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,

            Pageable pageable) {

        targetService.findTargetById(targetId);

        if (startDate == null && endDate == null) {
            endDate = Instant.now();
            startDate = endDate.minusSeconds(24 * 60 * 60);
        }

        return healthCheckService.findChecksByTargetId(targetId, startDate, endDate, pageable);
    }
}