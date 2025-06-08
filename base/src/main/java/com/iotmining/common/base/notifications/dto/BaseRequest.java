package com.iotmining.common.base.notifications.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseRequest {
    private String correlationId;
    private String sourceApp;
    private String userId;
    private int retryCount = 0;
    private Priority priority = Priority.MEDIUM;
    private long timestamp = System.currentTimeMillis();

    public enum Priority {
        HIGH, MEDIUM, LOW
    }
}
