package com.crossborder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.OrderStatus;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.OrderCreateDTO;
import com.crossborder.mall.entity.CbAddress;
import com.crossborder.mall.entity.CbCartItem;
import com.crossborder.mall.entity.CbOrder;
import com.crossborder.mall.entity.CbOrderItem;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbCartItemMapper;
import com.crossborder.mall.mapper.CbOrderItemMapper;
import com.crossborder.mall.mapper.CbOrderMapper;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.mapper.CbSeckillActivityMapper;
import com.crossborder.mall.mq.OrderCloseProducer;
import com.crossborder.mall.service.AddressService;
import com.crossborder.mall.service.CartService;
import com.crossborder.mall.service.MallOrderService;
import com.crossborder.mall.utils.RedisConstants;
import com.crossborder.mall.utils.RedisIdWorker;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MallOrderServiceImpl implements MallOrderService {

    private final CbOrderMapper orderMapper;
    private final CbOrderItemMapper orderItemMapper;
    private final CbCartItemMapper cartItemMapper;
    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;
    private final CbSeckillActivityMapper activityMapper;
    private final com.crossborder.mall.mapper.UserMapper userMapper;
    private final CartService cartService;
    private final AddressService addressService;
    private final RedisIdWorker redisIdWorker;
    private final OrderCloseProducer orderCloseProducer;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public CbOrder createFromCart(OrderCreateDTO dto) {
        Long userId = UserHolder.requireUser().getId();
        List<CbCartItem> cartItems = cartService.listCheckedFilled(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "请先勾选购物车商品");
        }
        CbAddress address = addressService.requireOwned(dto.getAddressId(), userId);

        String orderNo = "CB" + redisIdWorker.nextId("mall-order");
        BigDecimal total = BigDecimal.ZERO;
        List<CbOrderItem> orderItems = new ArrayList<>();

        for (CbCartItem cartItem : cartItems) {
            if (cartItem.getUnitPrice() == null || cartItem.getStock() == null
                    || cartItem.getQuantity() > cartItem.getStock()) {
                throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY,
                        "商品库存不足：" + cartItem.getProductName());
            }
            deductStock(cartItem.getProductId(), cartItem.getSkuId(), cartItem.getQuantity());

            BigDecimal line = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(line);

            CbOrderItem item = new CbOrderItem();
            item.setOrderNo(orderNo);
            item.setProductId(cartItem.getProductId());
            item.setSkuId(cartItem.getSkuId() == null ? 0L : cartItem.getSkuId());
            item.setProductName(cartItem.getProductName());
            item.setProductImage(cartItem.getProductImage());
            item.setSpecDesc(cartItem.getSpecDesc());
            item.setUnitPrice(cartItem.getUnitPrice());
            item.setQuantity(cartItem.getQuantity());
            item.setTotalPrice(line);
            orderItems.add(item);
        }

        CbOrder order = new CbOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.UNPAID);
        order.setSource(0);
        order.setReceiverName(address.getReceiverName());
        order.setReceiverPhone(address.getReceiverPhone());
        order.setReceiverAddress(formatAddress(address));
        order.setRemark(dto.getRemark());
        orderMapper.insert(order);

        for (CbOrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        List<Long> cartIds = cartItems.stream().map(CbCartItem::getId).toList();
        cartItemMapper.deleteByIds(cartIds);

        order.setItems(orderItems);
        scheduleCloseAfterCommit(orderNo);
        return order;
    }

    @Override
    public Page<CbOrder> pageMine(String status, int page, int size) {
        Long userId = UserHolder.requireUser().getId();
        Page<CbOrder> p = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50));
        Page<CbOrder> result = orderMapper.selectPage(p, new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getUserId, userId)
                .eq(StringUtils.hasText(status), CbOrder::getStatus, status)
                .orderByDesc(CbOrder::getId));
        result.getRecords().forEach(this::attachItems);
        return result;
    }

    @Override
    public CbOrder getMine(String orderNo) {
        Long userId = UserHolder.requireUser().getId();
        CbOrder order = requireByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        attachItems(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelMine(String orderNo, String reason) {
        Long userId = UserHolder.requireUser().getId();
        CbOrder order = requireByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        if (!OrderStatus.UNPAID.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仅待支付订单可取消");
        }
        closeOrder(order, StringUtils.hasText(reason) ? reason : "用户取消");
    }

    @Override
    @Transactional
    public void confirmMine(String orderNo) {
        Long userId = UserHolder.requireUser().getId();
        CbOrder order = requireByOrderNo(orderNo);
        if (!userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        OrderStatus.assertTransit(order.getStatus(), OrderStatus.COMPLETED);
        order.setStatus(OrderStatus.COMPLETED);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void closeIfUnpaid(String orderNo) {
        CbOrder order = orderMapper.selectOne(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getOrderNo, orderNo)
                .last("LIMIT 1"));
        if (order == null || !OrderStatus.UNPAID.equals(order.getStatus())) {
            return;
        }
        closeOrder(order, "超时未支付自动关闭");
    }

    @Override
    @Transactional
    public void markPaid(String orderNo) {
        CbOrder order = requireByOrderNo(orderNo);
        OrderStatus.assertTransit(order.getStatus(), OrderStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        order.setPayTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void ship(String orderNo) {
        CbOrder order = requireByOrderNo(orderNo);
        OrderStatus.assertTransit(order.getStatus(), OrderStatus.SHIPPED);
        order.setStatus(OrderStatus.SHIPPED);
        order.setShipTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    public Page<CbOrder> pageAdmin(String status, int page, int size) {
        Page<CbOrder> p = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50));
        Page<CbOrder> result = orderMapper.selectPage(p, new LambdaQueryWrapper<CbOrder>()
                .eq(StringUtils.hasText(status), CbOrder::getStatus, status)
                .orderByDesc(CbOrder::getId));
        result.getRecords().forEach(this::attachItems);
        return result;
    }

    @Override
    public CbOrder getAdmin(String orderNo) {
        CbOrder order = requireByOrderNo(orderNo);
        attachItems(order);
        return order;
    }

    @Override
    @Transactional
    public void cancelAdmin(String orderNo, String reason) {
        CbOrder order = requireByOrderNo(orderNo);
        if (!OrderStatus.UNPAID.equals(order.getStatus()) && !OrderStatus.PAID.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "当前状态不可取消");
        }
        closeOrder(order, StringUtils.hasText(reason) ? reason : "管理员取消");
    }

    @Override
    public Map<String, Object> adminDashboard() {
        Map<String, Object> map = new HashMap<>();
        map.put("productCount", productMapper.selectCount(null));
        map.put("userCount", userMapper.selectCount(null));
        map.put("orderCount", orderMapper.selectCount(null));
        map.put("unpaidCount", orderMapper.selectCount(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getStatus, OrderStatus.UNPAID)));
        map.put("paidCount", orderMapper.selectCount(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getStatus, OrderStatus.PAID)));
        map.put("shippedCount", orderMapper.selectCount(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getStatus, OrderStatus.SHIPPED)));

        List<CbOrder> paidOrders = orderMapper.selectList(new LambdaQueryWrapper<CbOrder>()
                .in(CbOrder::getStatus, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED));
        BigDecimal totalSales = paidOrders.stream()
                .map(CbOrder::getPayAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        map.put("totalSales", totalSales);
        return map;
    }

    private void closeOrder(CbOrder order, String reason) {
        OrderStatus.assertTransit(order.getStatus(), OrderStatus.CANCELLED);
        attachItems(order);
        if (order.getItems() != null) {
            for (CbOrderItem item : order.getItems()) {
                restoreStock(item.getProductId(), item.getSkuId(), item.getQuantity());
                stringRedisTemplate.delete(RedisConstants.CACHE_PRODUCT_KEY + item.getProductId());
            }
        }
        if (order.getActivityId() != null && order.getActivityId() > 0) {
            activityMapper.increaseStock(order.getActivityId(), 1);
            stringRedisTemplate.opsForValue().increment(RedisConstants.SECKILL_STOCK_KEY + order.getActivityId());
            stringRedisTemplate.opsForSet().remove(
                    RedisConstants.SECKILL_ORDER_KEY + order.getActivityId(),
                    String.valueOf(order.getUserId()));
            stringRedisTemplate.delete(RedisConstants.SECKILL_RESULT_KEY + order.getActivityId() + ":" + order.getUserId());
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setCancelReason(reason);
        orderMapper.updateById(order);
    }

    private void deductStock(Long productId, Long skuId, int qty) {
        if (skuId != null && skuId > 0) {
            if (skuMapper.decreaseStock(skuId, qty) == 0) {
                throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY, "SKU 库存不足");
            }
            productMapper.update(null, new LambdaUpdateWrapper<CbProduct>()
                    .eq(CbProduct::getId, productId)
                    .setSql("sales_count = sales_count + " + qty));
            stringRedisTemplate.delete(RedisConstants.CACHE_PRODUCT_KEY + productId);
            return;
        }
        if (productMapper.decreaseStock(productId, qty) == 0) {
            throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY, "商品库存不足");
        }
        stringRedisTemplate.delete(RedisConstants.CACHE_PRODUCT_KEY + productId);
    }

    private void restoreStock(Long productId, Long skuId, int qty) {
        if (skuId != null && skuId > 0) {
            skuMapper.increaseStock(skuId, qty);
            productMapper.update(null, new LambdaUpdateWrapper<CbProduct>()
                    .eq(CbProduct::getId, productId)
                    .setSql("sales_count = GREATEST(sales_count - " + qty + ", 0)"));
            return;
        }
        productMapper.increaseStock(productId, qty);
    }

    private void attachItems(CbOrder order) {
        List<CbOrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<CbOrderItem>()
                .eq(CbOrderItem::getOrderId, order.getId()));
        order.setItems(items);
    }

    private CbOrder requireByOrderNo(String orderNo) {
        CbOrder order = orderMapper.selectOne(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getOrderNo, orderNo)
                .last("LIMIT 1"));
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private String formatAddress(CbAddress address) {
        return String.join(" ",
                nullSafe(address.getProvince()),
                nullSafe(address.getCity()),
                nullSafe(address.getDistrict()),
                nullSafe(address.getDetail())).trim();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private void scheduleCloseAfterCommit(String orderNo) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            orderCloseProducer.sendCloseDelay(orderNo);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                orderCloseProducer.sendCloseDelay(orderNo);
            }
        });
    }
}
