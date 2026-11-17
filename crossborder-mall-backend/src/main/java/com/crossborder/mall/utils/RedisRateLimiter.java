package com.crossborder.mall.utils;

import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    public void check(String key, int max, int windowSeconds) {
        String redisKey = "rate:" + key;
        Long count = stringRedisTemplate.opsForValue().increment(redisKey);
        if (count != null && count == 1L) {
            stringRedisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }
        if (count != null && count > max) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "操作过于频繁，请稍后再试");
        }
    }
}
