package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetService {

    private final TargetRepository targetRepository;

    public TargetService(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public Target createTarget(CreateTargetRequest request) {
        Target newTarget = new Target();
        newTarget.setName(request.name());
        newTarget.setUrl(request.url());

        return targetRepository.save(newTarget);
    }

    public List<Target> findAllTargets() {
        return targetRepository.findAll();
    }
}