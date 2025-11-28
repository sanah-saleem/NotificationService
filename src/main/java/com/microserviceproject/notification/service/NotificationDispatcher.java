package com.microserviceproject.notification.service;

import com.microserviceproject.notification.model.NotificationChannelType;
import com.microserviceproject.notification.model.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NotificationDispatcher.class);

    private final Map<NotificationChannelType, INotificationChannel> channelMap =
            new EnumMap<>(NotificationChannelType.class);

    public NotificationDispatcher(List<INotificationChannel> channels) {
        for (INotificationChannel channel : channels) {
            channelMap.put(channel.getChannelType(), channel);
            log.info("Registered notification channel: {}", channel.getChannelType());
        }
    }

    public void dispatch(NotificationMessage message) {
        NotificationChannelType type = message.channelType();
        INotificationChannel channel = channelMap.get(type);
        if (channel == null) {
            log.error("No notification channel registered for type {}", type);
            throw new IllegalArgumentException("Unsupported notification channel: " + type);
        }
        log.info("Dispatching notification via channel={} to destination={}", type, message.destination());
        channel.send(message);
    }

}
