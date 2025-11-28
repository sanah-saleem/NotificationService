package com.microserviceproject.notification.controller;

import com.microserviceproject.notification.model.EmailRequest;
import com.microserviceproject.notification.integration.EmailNotificationProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailNotificationProducer producer;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        producer.sendEmailNotification(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Email notification queued for delivery");
    }

}
