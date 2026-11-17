package com.crossborder.mall.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_PRODUCT_TTL = 30L;
    public static final String CACHE_PRODUCT_KEY = "cache:product:";

    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    public static final String SECKILL_ORDER_KEY = "seckill:order:";
    public static final String SECKILL_RESULT_KEY = "seckill:result:";

    public static final String JWT_BLACKLIST_KEY = "jwt:blacklist:";
}
