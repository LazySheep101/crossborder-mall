package com.crossborder.mall.service;

import com.crossborder.mall.entity.CbPayment;

public interface PaymentService {
    CbPayment mockPay(String orderNo, String channel);

    CbPayment getByPaymentNo(String paymentNo);
}
