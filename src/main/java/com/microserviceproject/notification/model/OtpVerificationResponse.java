package com.microserviceproject.notification.model;

public record OtpVerificationResponse(
   OtpVerificationStatus status,
   String message
) {}
