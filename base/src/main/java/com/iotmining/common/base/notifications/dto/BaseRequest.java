package com.iotmining.common.base.notifications.dto;

import lombok.*;
import lombok.experimental.SuperBuilder; // Import SuperBuilder

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequest {
    private String correlationId;
    private String sourceApp;
    private String userId;

    @Builder.Default
    private int retryCount = 0;

    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Builder.Default
    private long timestamp = System.currentTimeMillis();

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
