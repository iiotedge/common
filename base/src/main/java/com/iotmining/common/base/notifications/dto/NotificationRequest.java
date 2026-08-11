package com.iotmining.common.base.notifications.dto;

import com.iotmining.common.base.notifications.enums.NotificationType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder; // Import SuperBuilder

@Getter
@Setter
@SuperBuilder
public class NotificationRequest<T> extends BaseRequest {
    private NotificationType type;
    private T payload;
}
