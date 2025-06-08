package com.iotmining.common.base.notifications.dto.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class WebSocketPayload {
    private String title;
    private Map<String, Object> metadata;
    private String message;
    private String type;
    private String url;
}

