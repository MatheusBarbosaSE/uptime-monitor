package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.dto.HealthCheckResponse;
import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;

    public HealthCheckService(HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    public List<HealthCheckResponse> findChecksByTargetId(Long targetId) {
        List<HealthCheck> checks = healthCheckRepository.findByTargetIdOrderByCheckedAtDesc(targetId);

        return checks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private HealthCheckResponse convertToResponse(HealthCheck healthCheck) {
        return new HealthCheckResponse(
                healthCheck.getId(),
                healthCheck.getCheckedAt(),
                healthCheck.getStatusCode(),
                healthCheck.getStatusMessage()
        );
    }
}