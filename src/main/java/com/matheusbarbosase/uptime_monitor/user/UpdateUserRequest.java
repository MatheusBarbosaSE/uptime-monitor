package com.matheusbarbosase.uptime_monitor.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Must be a valid email format")
        String email
) {
}