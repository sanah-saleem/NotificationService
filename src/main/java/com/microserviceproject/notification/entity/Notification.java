package com.microserviceproject.notification.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Notification {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String message;

    private String senderMail;

    @OneToMany
    private List<Recipient> recipients;

    private NotificationStatus status = NotificationStatus.PENDING;

    public Notification() {}

    public Notification(String message, String senderMail) {
        this.message = message;
        this.senderMail = senderMail;
    }

}
