package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class MonitoringService {

    private final TargetRepository targetRepository;
    private final RestTemplate restTemplate;

    public MonitoringService(TargetRepository targetRepository, RestTemplate restTemplate) {
        this.targetRepository = targetRepository;
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
        try {
            restTemplate.getForObject(target.getUrl(), String.class);

            System.out.println("SUCCESS: Site " + target.getName() + " (" + target.getUrl() + ") is ONLINE.");

        } catch (Exception e) {
            System.out.println("FAILURE: Site " + target.getName() + " (" + target.getUrl() + ") is OFFLINE or returning error: " + e.getMessage());
        }
    }
}