package com.crossborder.mall.service.impl;

import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.SeckillActivitySaveDTO;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.entity.CbProductSku;
import com.crossborder.mall.entity.CbSeckillActivity;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.mapper.CbSeckillActivityMapper;
import com.crossborder.mall.mq.SeckillOrderMessage;
import com.crossborder.mall.mq.SeckillOrderProducer;
import com.crossborder.mall.service.AddressService;
import com.crossborder.mall.service.SeckillActivityService;
import com.crossborder.mall.utils.RedisConstants;
import com.crossborder.mall.utils.RedisIdWorker;
import com.crossborder.mall.utils.RedisRateLimiter;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeckillActivityServiceImpl implements SeckillActivityService {

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT = new DefaultRedisScript<>();

    static {
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    private final CbSeckillActivityMapper activityMapper;
    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;
    private final AddressService addressService;
    private final RedisIdWorker redisIdWorker;
    private final SeckillOrderProducer seckillOrderProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisRateLimiter redisRateLimiter;

    @Override
    public List<CbSeckillActivity> listOnline() {
        LocalDateTime now = LocalDateTime.now();
        List<CbSeckillActivity> list = activityMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CbSeckillActivity>()
                .eq(CbSeckillActivity::getStatus, 1)
                .le(CbSeckillActivity::getStartTime, now)
                .ge(CbSeckillActivity::getEndTime, now)
                .orderByAsc(CbSeckillActivity::getEndTime));
        list.forEach(this::fillDisplay);
        return list;
    }

    @Override
    public CbSeckillActivity getDetail(Long id) {
        CbSeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "秒杀活动不存在");
        }
        fillDisplay(activity);
        String redisStock = stringRedisTemplate.opsForValue().get(RedisConstants.SECKILL_STOCK_KEY + id);
        if (redisStock != null) {
            try {
                activity.setStock(Integer.parseInt(redisStock));
            } catch (NumberFormatException ignored) {
                // 展示库内库存
            }
        }
        return activity;
    }

    @Override
    public Map<String, Object> placeOrder(Long activityId, Long addressId) {
        Long userId = UserHolder.requireUser().getId();
        redisRateLimiter.check("seckill:" + userId + ":" + activityId, 8, 10);
        CbSeckillActivity activity = requireOnlineWindow(activityId);
        if (addressId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请选择收货地址");
        }
        addressService.requireOwned(addressId, userId);
        ensureStockKey(activity);

        Long ret = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                activityId.toString(),
                userId.toString());
        int code = ret == null ? 1 : ret.intValue();
        if (code == 1) {
            throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY);
        }
        if (code == 2) {
            throw new BusinessException(ResultCode.SECKILL_DUPLICATE);
        }

        String orderNo = "CB" + redisIdWorker.nextId("mall-order");
        String requestId = UUID.randomUUID().toString().replace("-", "");
        seckillOrderProducer.send(SeckillOrderMessage.builder()
                .orderNo(orderNo)
                .userId(userId)
                .activityId(activityId)
                .addressId(addressId)
                .requestId(requestId)
                .build());

        Map<String, Object> data = new HashMap<>();
        data.put("status", "QUEUING");
        data.put("orderNo", orderNo);
        return data;
    }

    @Override
    public Map<String, Object> result(Long activityId) {
        Long userId = UserHolder.requireUser().getId();
        String value = stringRedisTemplate.opsForValue().get(resultKey(activityId, userId));
        Map<String, Object> data = new HashMap<>();
        if (value == null) {
            data.put("status", "QUEUING");
            return data;
        }
        if (value.startsWith("FAIL")) {
            data.put("status", "FAIL");
            data.put("message", value.substring(value.indexOf(':') + 1));
            return data;
        }
        data.put("status", "SUCCESS");
        data.put("orderNo", value);
        return data;
    }

    @Override
    public List<CbSeckillActivity> adminList() {
        List<CbSeckillActivity> list = activityMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CbSeckillActivity>()
                        .orderByDesc(CbSeckillActivity::getId));
        list.forEach(this::fillDisplay);
        return list;
    }

    @Override
    public CbSeckillActivity save(SeckillActivitySaveDTO dto) {
        CbSeckillActivity activity = dto.getId() == null ? new CbSeckillActivity()
                : activityMapper.selectById(dto.getId());
        if (dto.getId() != null && activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        activity.setTitle(dto.getTitle());
        activity.setProductId(dto.getProductId());
        activity.setSkuId(dto.getSkuId() == null ? 0L : dto.getSkuId());
        activity.setSeckillPrice(dto.getSeckillPrice());
        activity.setStock(dto.getStock());
        if (activity.getSold() == null) {
            activity.setSold(0);
        }
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setStatus(dto.getStatus() == null ? 0 : dto.getStatus());
        if (dto.getId() == null) {
            activityMapper.insert(activity);
        } else {
            activityMapper.updateById(activity);
        }
        if (activity.getStatus() != null && activity.getStatus() == 1) {
            preheat(activity.getId());
        }
        fillDisplay(activity);
        return activity;
    }

    @Override
    public void updateStatus(Long id, int status) {
        CbSeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        activity.setStatus(status);
        activityMapper.updateById(activity);
        if (status == 1) {
            preheat(id);
        } else {
            stringRedisTemplate.delete(RedisConstants.SECKILL_STOCK_KEY + id);
        }
    }

    @Override
    public void preheat(Long id) {
        CbSeckillActivity activity = activityMapper.selectById(id);
        if (activity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "活动不存在");
        }
        int stock = activity.getStock() == null ? 0 : activity.getStock();
        stringRedisTemplate.opsForValue().set(RedisConstants.SECKILL_STOCK_KEY + id, String.valueOf(stock));
    }

    private void ensureStockKey(CbSeckillActivity activity) {
        String key = RedisConstants.SECKILL_STOCK_KEY + activity.getId();
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return;
        }
        int stock = activity.getStock() == null ? 0 : activity.getStock();
        stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(stock));
    }

    private CbSeckillActivity requireOnlineWindow(Long activityId) {
        CbSeckillActivity activity = activityMapper.selectById(activityId);
        if (activity == null || activity.getStatus() == null || activity.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "活动未上线");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "不在秒杀时间窗内");
        }
        return activity;
    }

    private void fillDisplay(CbSeckillActivity activity) {
        CbProduct product = productMapper.selectById(activity.getProductId());
        if (product != null) {
            activity.setProductName(product.getName());
            activity.setProductImage(product.getMainImage());
            activity.setOriginPrice(product.getPrice());
        }
        if (activity.getSkuId() != null && activity.getSkuId() > 0) {
            CbProductSku sku = skuMapper.selectById(activity.getSkuId());
            if (sku != null) {
                activity.setSpecDesc(sku.getSpecDesc());
                activity.setOriginPrice(sku.getPrice());
            }
        } else {
            activity.setSpecDesc("默认规格");
        }
    }

    static String resultKey(Long activityId, Long userId) {
        return RedisConstants.SECKILL_RESULT_KEY + activityId + ":" + userId;
    }

    static void markFail(StringRedisTemplate redis, Long activityId, Long userId, String reason) {
        redis.opsForValue().set(resultKey(activityId, userId), "FAIL:" + reason, 1, TimeUnit.DAYS);
    }
}
