package com.crossborder.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.Result;
import com.crossborder.mall.entity.CbOrder;
import com.crossborder.mall.service.MallOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final MallOrderService mallOrderService;

    @GetMapping
    public Result<Page<CbOrder>> page(@RequestParam(required = false) String status,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return Result.success(mallOrderService.pageAdmin(status, page, size));
    }

    @GetMapping("/{orderNo}")
    public Result<CbOrder> detail(@PathVariable String orderNo) {
        return Result.success(mallOrderService.getAdmin(orderNo));
    }

    @PutMapping("/{orderNo}/ship")
    public Result<Void> ship(@PathVariable String orderNo) {
        mallOrderService.ship(orderNo);
        return Result.success();
    }

    @PutMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo,
                               @RequestParam(required = false) String reason) {
        mallOrderService.cancelAdmin(orderNo, reason);
        return Result.success();
    }
}
