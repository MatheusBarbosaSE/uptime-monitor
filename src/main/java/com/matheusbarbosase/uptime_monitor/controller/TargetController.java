package com.matheusbarbosase.uptime_monitor.controller;

import com.matheusbarbosase.uptime_monitor.dto.CreateTargetRequest;
import com.matheusbarbosase.uptime_monitor.dto.TargetResponse;
import com.matheusbarbosase.uptime_monitor.dto.UpdateTargetRequest;
import com.matheusbarbosase.uptime_monitor.service.TargetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.matheusbarbosase.uptime_monitor.service.EmailService;
import org.springframework.http.ResponseEntity;

import java.util.List;


@RestController
@RequestMapping("/api/targets")
public class TargetController {

    private final TargetService targetService;
    private final EmailService emailService;

    public TargetController(TargetService targetService, EmailService emailService) {
        this.targetService = targetService;
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        emailService.sendSimpleMessage(
                "test@example.com",
                "Test Alert - Uptime Monitor",
                "This is a test. If you are reading this, email sending works!"
        );
        return ResponseEntity.ok("Email send attempt registered. Check your Mailtrap inbox!");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TargetResponse createTarget(@Valid @RequestBody CreateTargetRequest request) {
        return targetService.createTarget(request);
    }

    @GetMapping
    public List<TargetResponse> findAllTargets() {
        return targetService.findAllTargets();
    }

    @GetMapping("/{id}")
    public TargetResponse findTargetById(@PathVariable("id") Long id) {
        return targetService.findTargetById(id);
    }

    @PutMapping("/{id}")
    public TargetResponse updateTarget(@PathVariable("id") Long id, @Valid @RequestBody UpdateTargetRequest request) {
        return targetService.updateTarget(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTarget(@PathVariable("id") Long id) {
        targetService.deleteTarget(id);
    }
}