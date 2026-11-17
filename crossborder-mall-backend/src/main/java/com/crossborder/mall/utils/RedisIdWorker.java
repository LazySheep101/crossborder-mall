package com.crossborder.mall.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class RedisIdWorker {

    private final StringRedisTemplate stringRedisTemplate;

    private static final long COUNT_BITS = 32;
    private static final long BEGIN_TIMESTAMP = 1640995200L;

    public long nextId(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        String day = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue()
                .increment("icr:" + prefix + ":" + day);

        return timestamp << COUNT_BITS | count;
    }
}
