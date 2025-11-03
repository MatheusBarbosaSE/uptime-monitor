package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.dto.TargetResponse;
import com.matheusbarbosase.uptime_monitor.dto.UpdateTargetRequest;
import com.matheusbarbosase.uptime_monitor.exception.ResourceNotFoundException;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.model.User;
import com.matheusbarbosase.uptime_monitor.repository.TargetRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TargetService {

    private final TargetRepository targetRepository;
    private final DynamicTaskScheduler taskScheduler;

    public TargetService(TargetRepository targetRepository, DynamicTaskScheduler taskScheduler) {
        this.targetRepository = targetRepository;
        this.taskScheduler = taskScheduler;
    }

    private TargetResponse convertToResponse(Target target) {
        return new TargetResponse(
                target.getId(),
                target.getName(),
                target.getUrl(),
                target.getCreatedAt(),
                target.getCheckInterval()
        );
    }

    @Transactional
    public TargetResponse createTarget(CreateTargetRequest request) {
        User currentUser = getAuthenticatedUser();

        Target newTarget = new Target();
        newTarget.setName(request.name());
        newTarget.setUrl(request.url());
        newTarget.setUser(currentUser);
        newTarget.setLastStatus("PENDING");
        newTarget.setCheckInterval(request.checkInterval());

        Target savedTarget = targetRepository.save(newTarget);

        taskScheduler.scheduleTask(savedTarget);

        return convertToResponse(savedTarget);
    }

    public List<TargetResponse> findAllTargets() {
        User currentUser = getAuthenticatedUser();
        List<Target> targets = targetRepository.findAllByUserId(currentUser.getId());
        return targets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public TargetResponse findTargetById(Long id) {
        User currentUser = getAuthenticatedUser();
        Target target = targetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Target not found with id: " + id));
        return convertToResponse(target);
    }

    @Transactional
    public TargetResponse updateTarget(Long id, UpdateTargetRequest request) {
        Target existingTarget = targetRepository.findByIdAndUserId(id, getAuthenticatedUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Target not found with id: " + id));

        existingTarget.setName(request.name());
        existingTarget.setUrl(request.url());
        existingTarget.setCheckInterval(request.checkInterval());

        Target updatedTarget = targetRepository.save(existingTarget);

        taskScheduler.rescheduleTask(updatedTarget);

        return convertToResponse(updatedTarget);
    }

    @Transactional
    public void deleteTarget(Long id) {
        Target targetToDelete = targetRepository.findByIdAndUserId(id, getAuthenticatedUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Target not found with id: " + id));

        taskScheduler.cancelTask(id);

        targetRepository.delete(targetToDelete);
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