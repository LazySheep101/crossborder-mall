package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.entity.CbPayment;
import com.crossborder.mall.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/mock")
    public Result<CbPayment> mockPay(@RequestBody Map<String, String> body) {
        return Result.success(paymentService.mockPay(body.get("orderNo"), body.get("channel")));
    }

    @GetMapping("/{paymentNo}")
    public Result<CbPayment> detail(@PathVariable String paymentNo) {
        return Result.success(paymentService.getByPaymentNo(paymentNo));
    }
}
