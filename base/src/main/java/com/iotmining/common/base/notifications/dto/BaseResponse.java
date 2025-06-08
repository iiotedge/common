package com.iotmining.common.base.notifications.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String correlationId;
    private boolean delivered;

    public static <T> BaseResponse<T> success(String cid, T data, boolean delivered) {
        BaseResponse<T> res = new BaseResponse<>();
        res.success = true;
        res.message = "OK";
        res.data = data;
        res.correlationId = cid;
        res.delivered = delivered;
        return res;
    }

    public static <T> BaseResponse<T> failure(String cid, String message) {
        BaseResponse<T> res = new BaseResponse<>();
        res.success = false;
        res.message = message;
        res.correlationId = cid;
        return res;
    }
}
