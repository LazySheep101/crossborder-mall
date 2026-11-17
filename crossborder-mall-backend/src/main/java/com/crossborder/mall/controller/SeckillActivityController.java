package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.entity.CbSeckillActivity;
import com.crossborder.mall.service.SeckillActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/seckill-activities")
@RequiredArgsConstructor
public class SeckillActivityController {

    private final SeckillActivityService seckillActivityService;

    @GetMapping
    public Result<List<CbSeckillActivity>> list() {
        return Result.success(seckillActivityService.listOnline());
    }

    @GetMapping("/{id}")
    public Result<CbSeckillActivity> detail(@PathVariable Long id) {
        return Result.success(seckillActivityService.getDetail(id));
    }

    @PostMapping("/{id}/orders")
    public Result<Map<String, Object>> placeOrder(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long addressId = body == null ? null : body.get("addressId");
        return Result.success(seckillActivityService.placeOrder(id, addressId));
    }

    @GetMapping("/{id}/result")
    public Result<Map<String, Object>> result(@PathVariable Long id) {
        return Result.success(seckillActivityService.result(id));
    }
}
