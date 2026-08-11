package com.iotmining.common.base.notifications.dto;

import com.iotmining.common.data.notifications.NotificationStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse{
    private String channel;
    private boolean delivered;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private String message;
    private NotificationStatus status;
    private UUID correlationId;
}
