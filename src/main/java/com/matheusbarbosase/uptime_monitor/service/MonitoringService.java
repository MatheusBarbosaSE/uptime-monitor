package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class MonitoringService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);
    private final TargetRepository targetRepository;
    private final HealthCheckRepository healthCheckRepository;
    private final RestTemplate restTemplate;

    public MonitoringService(TargetRepository targetRepository,
                             HealthCheckRepository healthCheckRepository,
                             RestTemplate restTemplate) {
        this.targetRepository = targetRepository;
        this.healthCheckRepository = healthCheckRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 60000)
    public void checkAllTargets() {
        log.info("--- RUNNING SCHEDULED CHECK ---");

        List<Target> targets = targetRepository.findAll();

        for (Target target : targets) {
            checkTarget(target);
        }

        log.info("--- CHECK FINISHED ---");
    }

    private void checkTarget(Target target) {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setTarget(target);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(target.getUrl(), String.class);

            healthCheck.setStatusCode(response.getStatusCode().value());

            if (response.getStatusCode().is2xxSuccessful()) {
                healthCheck.setStatusMessage("ONLINE");
                log.info("SUCCESS: Site {} is ONLINE ({})", target.getName(), response.getStatusCode().value());
            } else {
                healthCheck.setStatusMessage("UNEXPECTED_STATUS");
                log.warn("WARNING: Site {} returned {}", target.getName(), response.getStatusCode().value());
            }

        } catch (Exception e) {
            healthCheck.setStatusMessage("OFFLINE");
            log.error("FAILURE: Site {} is OFFLINE: {}", target.getName(), e.getMessage());
        }

        healthCheckRepository.save(healthCheck);
    }
}