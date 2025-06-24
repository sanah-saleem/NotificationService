package com.microserviceproject.notification.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproject.notification.dto.NotificationRequest;
import com.microserviceproject.notification.service.NotificationService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService; 

    @PostMapping("/email")
    public ResponseEntity<String> sendEmailNotification(@RequestBody NotificationRequest request) throws MessagingException {
        System.out.println("Entered controller");
        if(notificationService.checkIncomingRequest(request)) {
            notificationService.createAndSendNotification(request);
            return ResponseEntity.ok("Notification Processed");  
        }
        return ResponseEntity.ok("Invalid input");
    } 
    
}
