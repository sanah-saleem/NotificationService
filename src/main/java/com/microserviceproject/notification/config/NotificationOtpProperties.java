package com.microserviceproject.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "notification.otp")
public record NotificationOtpProperties (
        int length,
        long ttlSeconds,
        int maxAttempts,
        int maxPerDay
) {}
