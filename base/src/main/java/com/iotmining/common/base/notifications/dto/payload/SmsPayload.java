package com.iotmining.common.base.notifications.dto.payload;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SmsPayload {
    private String phoneNumber;
    private String content;

}
