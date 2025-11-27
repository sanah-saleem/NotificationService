package com.microserviceproject.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public record NotificationRequest (
        @NotBlank String message,
        @Email @NotBlank String recipientEmail,
        @NotBlank String subject
) {}
