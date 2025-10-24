package com.matheusbarbosase.uptime_monitor.auth;


public record RegisterRequest(
        String username,
        String password
) {
}