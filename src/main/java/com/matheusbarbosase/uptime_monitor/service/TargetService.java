package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.dto.UpdateTargetRequest;
import com.matheusbarbosase.uptime_monitor.exception.ResourceNotFoundException;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.model.User;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TargetService {

    private final TargetRepository targetRepository;

    public TargetService(TargetRepository targetRepository) {
        this.targetRepository = targetRepository;
    }

    public Target createTarget(CreateTargetRequest request) {
        User currentUser = getAuthenticatedUser();

        Target newTarget = new Target();
        newTarget.setName(request.name());
        newTarget.setUrl(request.url());

        newTarget.setUser(currentUser);

        return targetRepository.save(newTarget);
    }

    public List<Target> findAllTargets() {
        User currentUser = getAuthenticatedUser();

        return targetRepository.findAllByUserId(currentUser.getId());
    }

    public Target findTargetById(Long id) {
        User currentUser = getAuthenticatedUser();

        return targetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Target not found with id: " + id));
    }

    public Target updateTarget(Long id, UpdateTargetRequest request) {
        Target existingTarget = findTargetById(id);
        existingTarget.setName(request.name());
        existingTarget.setUrl(request.url());
        return targetRepository.save(existingTarget);
    }

    public void deleteTarget(Long id) {
        findTargetById(id);
        targetRepository.deleteById(id);
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        } else {
            throw new IllegalStateException("Authenticated principal is not an instance of User");
        }
    }
}