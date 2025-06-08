package com.iotmining.common.base.notifications.dto.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramPayload {
    private String chatId;
    private String message;
    private boolean markdown;

}
