package com.crossborder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crossborder.mall.common.OrderStatus;
import com.crossborder.mall.common.PaymentStatus;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.entity.CbOrder;
import com.crossborder.mall.entity.CbPayment;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbOrderMapper;
import com.crossborder.mall.mapper.CbPaymentMapper;
import com.crossborder.mall.service.MallOrderService;
import com.crossborder.mall.service.PaymentService;
import com.crossborder.mall.utils.RedisIdWorker;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CbPaymentMapper paymentMapper;
    private final CbOrderMapper orderMapper;
    private final MallOrderService mallOrderService;
    private final RedisIdWorker redisIdWorker;

    @Override
    @Transactional
    public CbPayment mockPay(String orderNo, String channel) {
        Long userId = UserHolder.requireUser().getId();
        CbOrder order = orderMapper.selectOne(new LambdaQueryWrapper<CbOrder>()
                .eq(CbOrder::getOrderNo, orderNo)
                .last("LIMIT 1"));
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        if (!OrderStatus.UNPAID.equals(order.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "订单不是待支付状态");
        }

        CbPayment existing = paymentMapper.selectOne(new LambdaQueryWrapper<CbPayment>()
                .eq(CbPayment::getOrderNo, orderNo)
                .eq(CbPayment::getStatus, PaymentStatus.SUCCESS)
                .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }

        String payChannel = normalizeChannel(channel);
        CbPayment payment = new CbPayment();
        payment.setPaymentNo("PAY" + redisIdWorker.nextId("mall-pay"));
        payment.setOrderNo(orderNo);
        payment.setUserId(userId);
        payment.setAmount(order.getPayAmount());
        payment.setChannel(payChannel);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTradeNo(payChannel + "-" + System.currentTimeMillis());
        payment.setPaidAt(LocalDateTime.now());
        paymentMapper.insert(payment);

        mallOrderService.markPaid(orderNo);
        return payment;
    }

    private String normalizeChannel(String channel) {
        if (channel == null || channel.isBlank()) {
            return "MOCK";
        }
        String upper = channel.trim().toUpperCase();
        return switch (upper) {
            case "ALIPAY", "WECHAT", "STRIPE", "MOCK" -> upper;
            default -> "MOCK";
        };
    }

    @Override
    public CbPayment getByPaymentNo(String paymentNo) {
        CbPayment payment = paymentMapper.selectOne(new LambdaQueryWrapper<CbPayment>()
                .eq(CbPayment::getPaymentNo, paymentNo)
                .last("LIMIT 1"));
        if (payment == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "支付单不存在");
        }
        Long userId = UserHolder.requireUser().getId();
        if (!userId.equals(payment.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "支付单不存在");
        }
        return payment;
    }
}
