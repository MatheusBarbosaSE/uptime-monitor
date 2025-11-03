package com.matheusbarbosase.uptime_monitor.service;

import com.matheusbarbosase.uptime_monitor.model.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


@Service
public class DynamicTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(DynamicTaskScheduler.class);

    private final TaskScheduler taskScheduler;
    private final MonitoringService monitoringService;

    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DynamicTaskScheduler(TaskScheduler taskScheduler, MonitoringService monitoringService) {
        this.taskScheduler = taskScheduler;
        this.monitoringService = monitoringService;
    }

    public void scheduleTask(Target target) {
        Runnable task = () -> monitoringService.checkTargetNow(target);

        long intervalInMillis = Duration.ofMinutes(target.getCheckInterval()).toMillis();

        ScheduledFuture<?> scheduledTask = taskScheduler.scheduleAtFixedRate(task, intervalInMillis);

        scheduledTasks.put(target.getId(), scheduledTask);
        log.info("Scheduled check for target ID {}: {} (every {} minutes)",
                target.getId(), target.getName(), target.getCheckInterval());
    }

    public void cancelTask(Long targetId) {
        ScheduledFuture<?> task = scheduledTasks.get(targetId);
        if (task != null) {
            task.cancel(false);
            scheduledTasks.remove(targetId);
            log.info("Cancelled check for target ID {}", targetId);
        }
    }

    public void rescheduleTask(Target target) {
        log.info("Rescheduling task for target ID {}", target.getId());
        cancelTask(target.getId());
        scheduleTask(target);
    }
}