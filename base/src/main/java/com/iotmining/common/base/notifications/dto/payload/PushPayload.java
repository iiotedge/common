package com.iotmining.common.base.notifications.dto.payload;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushPayload {
    private String deviceToken;
    private String title;
    private String message;
    private String clickActionUrl;
}
