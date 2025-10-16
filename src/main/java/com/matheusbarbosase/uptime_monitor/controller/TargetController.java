package com.matheusbarbosase.uptime_monitor.controller;

import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.model.Target;
import com.matheusbarbosase.uptime_monitor.service.TargetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/targets")
public class TargetController {

    private final TargetService targetService;

    public TargetController(TargetService targetService) {
        this.targetService = targetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Target createTarget(@RequestBody CreateTargetRequest request) {
        return targetService.createTarget(request);
    }

    @GetMapping
    public List<Target> findAllTargets() {
        return targetService.findAllTargets();
    }

    @GetMapping("/{id}")
    public Target findTargetById(@PathVariable("id") Long id) {
        return targetService.findTargetById(id);
    }
}