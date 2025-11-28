package com.microserviceproject.notification.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OtpRequest (
        @NotBlank String userId,
        @NotBlank String purpose,
        @NotBlank @Email String email
) {}
