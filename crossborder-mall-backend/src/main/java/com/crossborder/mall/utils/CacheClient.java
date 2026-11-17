package com.crossborder.mall.utils;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


@Slf4j
@Component
@RequiredArgsConstructor
public class CacheClient {


    private final StringRedisTemplate stringRedisTemplate;


    public void set(String key, Object value, Long time, TimeUnit unit) {

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }


    public <R,ID> R queryShopWithPassCache(String keyPrefix, ID id, Class<R> type,
                                           Function<ID,R> dbFallback,
                                           Long time, TimeUnit timeUnit) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        //如果缓存中存在数据，则直接返回
        if (StrUtil.isNotBlank(json)) {
            return JSONUtil.toBean(json, type);
        }
        //如果缓存中存在""，则直接返回
        if (json != null) {
            return null;
        }
        //数据库查询商铺信息
        R r = dbFallback.apply(id);
        if (r == null) {
            //将空值缓存到Redis中，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key,"",
                    RedisConstants.CACHE_NULL_TTL,TimeUnit.MINUTES);
            return null;
        }
        this.set(key, r, time, timeUnit);
        return r;
    }



    private static final ExecutorService CACHE_REBUILD_EXECUTOR =
            Executors.newFixedThreadPool(10);
    public <R,ID> R queryShopWithLogicExpire(String keyPrefix,ID id, Class<R> type,
                                          Function<ID,R> dbFallback,
                                             Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(json)) {
            return null;
        }

        RedisData redisData = JSONUtil.toBean(json, RedisData.class);

        JSONObject data = (JSONObject) redisData.getData();

        R r = JSONUtil.toBean(data, type);

        LocalDateTime expireTime = redisData.getExpireTime();

        if (expireTime.isAfter(LocalDateTime.now())){
            return r;
        }

        String lockKey = "lock:cache:" + id;
        boolean isLock = tryLock(lockKey);

        if (isLock) {
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 重建缓存
                    R r1 = dbFallback.apply(id);
                    this.setWithLogicalExpire(key, r1, time,unit);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    releaseLock(lockKey);
                }
            });
        }
        // 无论是否抢到锁，都返回旧数据（逻辑过期）
        return r;
    }

    private boolean tryLock(String key){

        Boolean flag = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", 10, TimeUnit.SECONDS);

        return BooleanUtil.isTrue(flag);
    }

    private void releaseLock(String key){
        stringRedisTemplate.delete(key);

    }

}
