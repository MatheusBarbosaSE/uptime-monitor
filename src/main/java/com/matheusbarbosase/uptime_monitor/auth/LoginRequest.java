package com.matheusbarbosase.uptime_monitor.auth;


public record LoginRequest(
        String username,
        String password
) {
}