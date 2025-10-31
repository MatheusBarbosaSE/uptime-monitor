package com.matheusbarbosase.uptime_monitor.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record RegisterRequest(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email format")
        String email
) {
}