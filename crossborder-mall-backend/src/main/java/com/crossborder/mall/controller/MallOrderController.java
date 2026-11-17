package com.crossborder.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.OrderCreateDTO;
import com.crossborder.mall.entity.CbOrder;
import com.crossborder.mall.service.MallOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class MallOrderController {

    private final MallOrderService mallOrderService;

    @PostMapping
    public Result<CbOrder> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(mallOrderService.createFromCart(dto));
    }

    @GetMapping
    public Result<Page<CbOrder>> page(@RequestParam(required = false) String status,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        return Result.success(mallOrderService.pageMine(status, page, size));
    }

    @GetMapping("/{orderNo}")
    public Result<CbOrder> detail(@PathVariable String orderNo) {
        return Result.success(mallOrderService.getMine(orderNo));
    }

    @PutMapping("/{orderNo}/cancel")
    public Result<Void> cancel(@PathVariable String orderNo,
                               @RequestParam(required = false) String reason) {
        mallOrderService.cancelMine(orderNo, reason);
        return Result.success();
    }

    @PutMapping("/{orderNo}/confirm")
    public Result<Void> confirm(@PathVariable String orderNo) {
        mallOrderService.confirmMine(orderNo);
        return Result.success();
    }
}
