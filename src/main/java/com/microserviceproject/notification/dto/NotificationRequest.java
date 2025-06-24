package com.microserviceproject.notification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {
    
    String message;
    String senderEmail;
    String recipientEmail;
    String subject;

    public NotificationRequest() {}

    public NotificationRequest(String message, String senderEmail, String recipientEmail, String subject) {
        this.message = message;
        this.senderEmail = senderEmail;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
    }

}
