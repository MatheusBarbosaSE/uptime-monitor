package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.model.HealthCheck;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.model.User;
import com.matheusbarbosase.uptime_monitor.repository.HealthCheckRepository;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


@Service
public class MonitoringService {

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);

    private final TargetRepository targetRepository;
    private final HealthCheckRepository healthCheckRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    public MonitoringService(TargetRepository targetRepository,
                             HealthCheckRepository healthCheckRepository,
                             EmailService emailService,
                             RestTemplate restTemplate) {
        this.targetRepository = targetRepository;
        this.healthCheckRepository = healthCheckRepository;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public void checkTargetNow(Target target) {
        log.info("Checking target: {}", target.getName());

        HealthCheck healthCheck = new HealthCheck();
        healthCheck.setTarget(target);

        String newStatus;
        Integer statusCode = null;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(target.getUrl(), String.class);
            statusCode = response.getStatusCode().value();
            healthCheck.setStatusCode(statusCode);

            if (response.getStatusCode().is2xxSuccessful()) {
                newStatus = "ONLINE";
                log.info("SUCCESS: Site {} is ONLINE ({})", target.getName(), statusCode);
            } else {
                newStatus = "UNEXPECTED_STATUS";
                log.warn("WARNING: Site {} returned {}", target.getName(), statusCode);
            }

        } catch (Exception e) {
            newStatus = "OFFLINE";
            log.error("FAILURE: Site {} is OFFLINE: {}", target.getName(), e.getMessage());
        }

        String oldStatus = target.getLastStatus();

        if (!newStatus.equals(oldStatus)) {
            log.warn("Status change detected for {}: {} -> {}", target.getName(), oldStatus, newStatus);

            if (newStatus.equals("OFFLINE")) {
                sendAlertEmail(target.getUser(), target, newStatus);
            }
            else if (newStatus.equals("ONLINE") && "OFFLINE".equals(oldStatus)) {
                sendRecoveryEmail(target.getUser(), target);
            }

            target.setLastStatus(newStatus);
            targetRepository.save(target);
        }

        healthCheck.setStatusMessage(newStatus);
        healthCheckRepository.save(healthCheck);
    }

    private void sendAlertEmail(User user, Target target, String newStatus) {
        String userEmail = user.getEmail();
        String subject = "[Uptime Monitor Alert] Your site " + target.getName() + " is DOWN!";
        String text = "Hello " + user.getUsername() + ",\n\n"
                + "This is an automatic alert from Uptime Monitor.\n\n"
                + "Your site " + target.getName() + " (" + target.getUrl() + ") "
                + "is currently unreachable. \n\n"
                + "Status detected: " + newStatus;

        emailService.sendSimpleMessage(userEmail, subject, text);
    }

    private void sendRecoveryEmail(User user, Target target) {
        String userEmail = user.getEmail();
        String subject = "[Uptime Monitor Recovery] Your site " + target.getName() + " is back ONLINE!";
        String text = "Hello " + user.getUsername() + ",\n\n"
                + "Good news!\n\n"
                + "Your site " + target.getName() + " (" + target.getUrl() + ") "
                + "has recovered and is now back ONLINE.";

        emailService.sendSimpleMessage(userEmail, subject, text);
    }
}