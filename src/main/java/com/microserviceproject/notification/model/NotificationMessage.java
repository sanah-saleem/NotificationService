package com.microserviceproject.notification.model;

import java.util.Map;

public record NotificationMessage (
        NotificationChannelType channelType,
        String destination,  // email address, phone number, etc.
        String subject,     // mainly for email; optional for SMS/WhatsApp
        String body,
        Map<String, Object> metadata // optional extra info
){}
