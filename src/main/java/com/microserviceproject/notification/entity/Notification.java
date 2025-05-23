package com.microserviceproject.notification.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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

    private String recieverMail;

    public Notification() {}

    public Notification(String message, String senderMail, String recieverMail) {
        this.message = message;
        this.senderMail = senderMail;
        this.recieverMail = recieverMail;
    }

}
