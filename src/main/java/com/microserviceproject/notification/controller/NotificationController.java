package com.microserviceproject.notification.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproject.notification.dto.NotificationRequest;
import com.microserviceproject.notification.service.NotificationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService; 

    @PostMapping("/email")
    public ResponseEntity<String> sendEmailNotification(@Valid @RequestBody NotificationRequest request) throws MessagingException {
        System.out.println("Received notification request for recipient: " + request.getRecipientEmail());
        if(notificationService.checkIncomingRequest(request)) {
            notificationService.createAndSendNotification(request);
            return ResponseEntity.ok("Notification Processed");  
        }
        return ResponseEntity.badRequest().body("Invalid input");
    } 
    
}
