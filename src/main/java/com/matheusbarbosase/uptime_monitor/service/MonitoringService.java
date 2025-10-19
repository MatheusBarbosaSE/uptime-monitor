package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class MonitoringService {

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

    @Scheduled(fixedRate = 10000)
    public void checkAllTargets() {
        System.out.println("--- RUNNING SCHEDULED CHECK ---");

        List<Target> targets = targetRepository.findAll();

        for (Target target : targets) {
            checkTarget(target);
        }

        System.out.println("--- CHECK FINISHED ---");
    }

    private void checkTarget(Target target) {
        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setTarget(target);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(target.getUrl(), String.class);

            healthCheck.setStatusCode(response.getStatusCode().value());

            if (response.getStatusCode().is2xxSuccessful()) {
                healthCheck.setStatusMessage("ONLINE");
                System.out.println("SUCCESS: Site " + target.getName() + " is ONLINE (" + response.getStatusCode().value() + ")");
            } else {
                healthCheck.setStatusMessage("UNEXPECTED_STATUS");
                System.out.println("WARNING: Site " + target.getName() + " returned " + response.getStatusCode().value());
            }

        } catch (Exception e) {
            healthCheck.setStatusMessage("OFFLINE");
            System.out.println("FAILURE: Site " + target.getName() + " is OFFLINE: " + e.getMessage());
        }

        healthCheckRepository.save(healthCheck);
    }
}