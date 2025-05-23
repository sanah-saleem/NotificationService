package com.microserviceproject.notification.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.microserviceproject.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID>{
}
