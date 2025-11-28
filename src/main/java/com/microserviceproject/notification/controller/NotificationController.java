package com.microserviceproject.notification.controller;

import com.microserviceproject.notification.model.EmailRequest;
import com.microserviceproject.notification.integration.EmailNotificationProducer;
import com.microserviceproject.notification.model.NotificationChannelType;
import com.microserviceproject.notification.model.NotificationMessage;
import com.microserviceproject.notification.service.NotificationDispatcher;
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

    private final NotificationDispatcher dispatcher;

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@Valid @RequestBody EmailRequest request) {
        NotificationMessage message = new NotificationMessage(
                NotificationChannelType.EMAIL,
                request.to(),
                request.subject(),
                request.body(),
                null
        );
        dispatcher.dispatch(message);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body("Email notification queued for delivery");
    }

}
