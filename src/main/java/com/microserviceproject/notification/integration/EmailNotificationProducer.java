package com.microserviceproject.notification.integration;

import com.microserviceproject.notification.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationProducer.class);
    private final KafkaTemplate<String, EmailRequest> kafkaTemplate;
    private final String emailTopicName;

    public EmailNotificationProducer(
            KafkaTemplate<String, EmailRequest> kafkaTemplate,
            @Value("${notification.topics.email}") String emailTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.emailTopicName = emailTopicName;
    }

    public void sendEmailNotification(EmailRequest emailRequest) {
        log.info("Publishing email notification to Kafka topic {} for recipient {}",
                emailTopicName, emailRequest.to());
        kafkaTemplate.send(emailTopicName, emailRequest.to(), emailRequest)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish email notification to Kafka", ex);
                    } else {
                        log.info("Published email notification to Kafka partition={}, offset={}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

}
