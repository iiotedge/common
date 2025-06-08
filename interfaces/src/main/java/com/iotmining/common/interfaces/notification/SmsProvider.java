package com.iotmining.common.interfaces.notification;

//import com.iotmining.common.base.notifications.NotificationDtoImpl;
import com.iotmining.common.base.notifications.dto.NotificationWrapper;
import com.iotmining.common.base.notifications.dto.payload.SmsPayload;

public interface SmsProvider {
//    void send(NotificationDtoImpl smsRequest);
    void send(NotificationWrapper<SmsPayload> request);
}
