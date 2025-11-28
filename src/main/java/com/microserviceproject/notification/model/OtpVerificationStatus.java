package com.microserviceproject.notification.model;

public enum OtpVerificationStatus {
    VALID,
    INVALID,
    EXPIRED_OR_NOT_FOUND,
    TOO_MANY_ATTEMPTS
}
