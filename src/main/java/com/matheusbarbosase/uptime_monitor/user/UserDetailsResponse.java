package com.matheusbarbosase.uptime_monitor.user;

public record UserDetailsResponse(
        Long id,
        String username,
        String email
) {
}