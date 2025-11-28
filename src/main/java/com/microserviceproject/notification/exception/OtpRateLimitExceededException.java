package com.microserviceproject.notification.exception;

public class OtpRateLimitExceededException extends RuntimeException{
    public OtpRateLimitExceededException(String message) {
        super(message);
    }
}
