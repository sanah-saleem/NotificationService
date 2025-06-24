package com.microserviceproject.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microserviceproject.notification.dto.NotificationRequest;

import jakarta.mail.MessagingException;

@Service
public class NotificationService {
    
    @Autowired
    EmailService emailService;

    public void createAndSendNotification(NotificationRequest request) throws MessagingException {
        System.out.println("Entered Notification service");
        System.out.println(request.toString());
        emailService.sendEmail(request.getRecipientEmail(), request.getSubject(), request.getMessage());
    }

    public boolean checkIncomingRequest(NotificationRequest request) {
        if(request == null) {
            return false;
        }
        if(request.getRecipientEmail() == null) {
            return false;
        }
        return true;
    }

}
