package com.iotmining.common.base.notifications.dto;

import com.iotmining.common.base.notifications.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationWrapper<T> {
    private UUID userId;
    private NotificationType type;
    private T payload;
    private UUID correlationId;
    private String sourceApp;
    private int retryCount;
    private BaseRequest.Priority priority;
    private long timestamp;
}
