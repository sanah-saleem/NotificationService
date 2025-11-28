package com.microserviceproject.notification.integration;

import com.microserviceproject.notification.model.EmailRequest;
import com.microserviceproject.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);
    private final EmailService emailService;

    public EmailNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${notification.topics.email}", groupId = "notification-service-group")
    public void consumeEmailNotification(EmailRequest request) {
        log.info("Received email notification from Kafka for recipient {}", request.to());
        try {
            emailService.sendEmail(request);
            log.info("Email sent successfully to {}", request.to());
        } catch (Exception ex){
            log.error("Failed to send email to {}", request.to(), ex);
            // Later: add retry / dead-letter topic logic.
        }
    }

}
