package com.iotmining.common.interfaces.notification;


//import com.iotmining.common.base.notifications.NotificationDtoImpl;
import com.iotmining.common.base.notifications.dto.BaseRequest;
import com.iotmining.common.base.notifications.dto.NotificationResponse;
import com.iotmining.common.data.notifications.NotificationChannel;

public interface NotificationSender {
    boolean supports(NotificationChannel channel);
    NotificationResponse send(BaseRequest request);
}
