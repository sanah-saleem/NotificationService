package com.microserviceproject.notification.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.mail.MailException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OtpRateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleOtpRateLimit(OtpRateLimitExceededException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "OTP_RATE_LIMIT_EXCEEDED");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "VALIDATION_FAILED");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        body.put("details", fieldErrors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, Object>> handleMailException(MailException ex) {
        log.error("Mail sending failed", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "MAIL_SEND_FAILED");
        body.put("message", "Failed to send email notification");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<Map<String, Object>> handleKafkaException(KafkaException ex) {
        log.error("Kafka error", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "KAFKA_ERROR");
        body.put("message", "Failed to queue notification. Please try again later.");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INTERNAL_SERVER_ERROR");
        body.put("message", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

}
