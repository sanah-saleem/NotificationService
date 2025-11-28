package com.microserviceproject.notification.service;

import com.microserviceproject.notification.model.NotificationChannelType;
import com.microserviceproject.notification.model.NotificationMessage;

public interface INotificationChannel {

    NotificationChannelType getChannelType();
    void send(NotificationMessage message);

}
