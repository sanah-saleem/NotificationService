package com.microserviceproject.notification.model;

import jakarta.validation.constraints.NotBlank;

public record OtpVerifyRequest(
        @NotBlank String userId,
        @NotBlank String purpose,
        @NotBlank String otp
) {}
