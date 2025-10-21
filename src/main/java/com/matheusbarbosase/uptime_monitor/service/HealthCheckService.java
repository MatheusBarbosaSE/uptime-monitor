package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.dto.HealthCheckResponse;
import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class HealthCheckService {

    private final HealthCheckRepository healthCheckRepository;

    public HealthCheckService(HealthCheckRepository healthCheckRepository) {
        this.healthCheckRepository = healthCheckRepository;
    }

    public Page<HealthCheckResponse> findChecksByTargetId(Long targetId, Pageable pageable) {
        Page<HealthCheck> checksPage = healthCheckRepository.findByTargetId(targetId, pageable);
        return checksPage.map(this::convertToResponse);
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