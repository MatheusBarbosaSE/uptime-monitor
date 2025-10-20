package com.matheusbarbosase.uptime_monitor.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;


public record UpdateTargetRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "URL cannot be blank")
        @URL(message = "Must be a valid URL format")
        String url
) {
}