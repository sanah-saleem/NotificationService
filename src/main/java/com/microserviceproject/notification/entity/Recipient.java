package com.microserviceproject.notification.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Recipient {

    @Id
    @GeneratedValue
    private UUID id;

    private String email;

    private String name;

    public Recipient() {}

    public Recipient(String email, String name) {
        this.email = email;
        this.name = name;
    }

}