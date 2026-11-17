package com.crossborder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crossborder.mall.common.OrderStatus;
import com.crossborder.mall.entity.CbAddress;
import com.crossborder.mall.entity.CbOrder;
import com.crossborder.mall.entity.CbOrderItem;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.entity.CbSeckillActivity;
import com.crossborder.mall.mapper.CbOrderItemMapper;
import com.crossborder.mall.mapper.CbOrderMapper;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.mapper.CbSeckillActivityMapper;
import com.crossborder.mall.mq.OrderCloseProducer;
import com.crossborder.mall.mq.SeckillOrderMessage;
import com.crossborder.mall.service.AddressService;
import com.crossborder.mall.utils.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SeckillOrderPersistService {

    private final CbSeckillActivityMapper activityMapper;
    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;
    private final CbOrderMapper orderMapper;
    private final CbOrderItemMapper orderItemMapper;
    private final AddressService addressService;
    private final OrderCloseProducer orderCloseProducer;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * @return true 已落库或已处理过；false 库存不足，调用方回补 Redis
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean persist(SeckillOrderMessage payload) {
        Boolean first = stringRedisTemplate.opsForValue()
                .setIfAbsent("seckill:msg:done:" + payload.getRequestId(), "1", 1, TimeUnit.DAYS);
        if (Boolean.FALSE.equals(first)) {
            return true;
        }
        Long existed = orderMapper.selectCount(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getUserId, payload.getUserId())
                .eq(CbOrder::getActivityId, payload.getActivityId())
                .ne(CbOrder::getStatus, OrderStatus.CANCELLED));
        if (existed != null && existed > 0) {
            return true;
        }
        if (activityMapper.decreaseStock(payload.getActivityId()) == 0) {
            return false;
        }
        CbSeckillActivity activity = activityMapper.selectById(payload.getActivityId());
        long skuId = activity.getSkuId() == null ? 0L : activity.getSkuId();
        boolean stockOk = skuId > 0
                ? skuMapper.decreaseStock(skuId, 1) > 0
                : productMapper.decreaseStock(activity.getProductId(), 1) > 0;
        if (!stockOk) {
            throw new IllegalStateException("商品库存不足");
        }
        if (skuId > 0) {
            productMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.crossborder.mall.entity.CbProduct>()
                    .eq(com.crossborder.mall.entity.CbProduct::getId, activity.getProductId())
                    .setSql("sales_count = sales_count + 1"));
        }

        CbAddress address = addressService.requireOwned(payload.getAddressId(), payload.getUserId());
        BigDecimal price = activity.getSeckillPrice();
        CbOrder order = new CbOrder();
        order.setOrderNo(payload.getOrderNo());
        order.setUserId(payload.getUserId());
        order.setTotalAmount(price);
        order.setPayAmount(price);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.UNPAID);
        order.setSource(1);
        order.setActivityId(payload.getActivityId());
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(joinAddress(address));
        order.setRemark("秒杀订单#" + payload.getActivityId());
        orderMapper.insert(order);

        CbProduct product = productMapper.selectById(activity.getProductId());
        CbOrderItem item = new CbOrderItem();
        item.setOrderId(order.getId());
        item.setOrderNo(payload.getOrderNo());
        item.setProductId(activity.getProductId());
        item.setSkuId(skuId);
        item.setProductName(product == null ? activity.getTitle() : product.getName());
        item.setProductImage(product == null ? null : product.getMainImage());
        item.setSpecDesc(skuId > 0 ? "秒杀规格" : "默认规格");
        item.setUnitPrice(price);
        item.setQuantity(1);
        item.setTotalPrice(price);
        orderItemMapper.insert(item);

        stringRedisTemplate.opsForValue().set(
                SeckillActivityServiceImpl.resultKey(payload.getActivityId(), payload.getUserId()),
                payload.getOrderNo(),
                1,
                TimeUnit.DAYS);
        stringRedisTemplate.delete(RedisConstants.CACHE_PRODUCT_KEY + activity.getProductId());
        orderCloseProducer.sendCloseDelay(payload.getOrderNo());
        return true;
    }

    private String joinAddress(CbAddress address) {
        return String.join(" ",
                empty(address.getProvince()),
                empty(address.getCity()),
                empty(address.getDistrict()),
                empty(address.getDetail())).trim();
    }

    private String empty(String value) {
        return value == null ? "" : value;
    }

    public void compensateRedis(SeckillOrderMessage payload) {
        stringRedisTemplate.opsForValue().increment(RedisConstants.SECKILL_STOCK_KEY + payload.getActivityId());
        stringRedisTemplate.opsForSet().remove(
                RedisConstants.SECKILL_ORDER_KEY + payload.getActivityId(),
                String.valueOf(payload.getUserId()));
        stringRedisTemplate.delete("seckill:msg:done:" + payload.getRequestId());
        SeckillActivityServiceImpl.markFail(stringRedisTemplate, payload.getActivityId(), payload.getUserId(), "库存不足");
    }
}
