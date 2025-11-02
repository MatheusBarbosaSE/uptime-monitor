package com.matheusbarbosase.uptime_monitor.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;


public record CreateTargetRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @NotBlank(message = "URL cannot be blank")
        @URL(message = "Must be a valid URL format")
        String url,

        @NotNull(message = "Interval cannot be null")
        @Min(value = 1, message = "Interval must be at least 1 minute")
        @Max(value = 1440, message = "Interval cannot be more than 1440 minutes (24 hours)")
        Integer checkInterval
) {
}