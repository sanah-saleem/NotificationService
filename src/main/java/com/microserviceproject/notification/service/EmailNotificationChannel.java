package com.microserviceproject.notification.service;

import com.microserviceproject.notification.integration.EmailNotificationProducer;
import com.microserviceproject.notification.model.EmailRequest;
import com.microserviceproject.notification.model.NotificationChannelType;
import com.microserviceproject.notification.model.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailNotificationChannel implements INotificationChannel{

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationChannel.class);

    private final EmailNotificationProducer emailNotificationProducer;

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.EMAIL;
    }

    @Override
    public void send(NotificationMessage message) {
        String to = message.destination();;
        String subject = message.subject();
        String body = message.body();
        if (to == null || to.isBlank()) {
            log.warn("EmailNotificationChannel: destination is null/blank, skipping send.");
            return;
        }
        EmailRequest request = new EmailRequest(to, subject, body);
        emailNotificationProducer.sendEmailNotification(request);
    }
}
