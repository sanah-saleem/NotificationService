package com.microserviceproject.notification.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microserviceproject.notification.entity.Notification;
import com.microserviceproject.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @PostMapping()
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationRepository.save(notification);
    } 

    @GetMapping()
    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }
    
    
}
