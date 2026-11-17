package com.crossborder.mall.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "bad request"),
    UNAUTHORIZED(401, "unauthorized"),
    FORBIDDEN(403, "forbidden"),
    NOT_FOUND(404, "not found"),
    TOO_MANY_REQUESTS(429, "too many requests"),
    INTERNAL_ERROR(500, "internal error"),

    INVALID_PHONE(40001, "手机号格式错误"),
    INVALID_CODE(40002, "验证码错误"),
    USER_NOT_FOUND(40003, "用户不存在"),
    SHOP_NOT_FOUND(40101, "店铺不存在"),
    VOUCHER_NOT_FOUND(40201, "优惠券不存在"),
    SECKILL_FAIL(40202, "秒杀失败"),
    SECKILL_STOCK_EMPTY(40203, "库存不足"),
    SECKILL_DUPLICATE(40204, "不能重复下单"),
    SECKILL_QUEUING(40205, "排队处理中");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
