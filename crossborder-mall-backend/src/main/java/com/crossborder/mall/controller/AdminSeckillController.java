package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.SeckillActivitySaveDTO;
import com.crossborder.mall.entity.CbSeckillActivity;
import com.crossborder.mall.service.SeckillActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/seckill")
@RequiredArgsConstructor
public class AdminSeckillController {

    private final SeckillActivityService seckillActivityService;

    @GetMapping
    public Result<List<CbSeckillActivity>> list() {
        return Result.success(seckillActivityService.adminList());
    }

    @PostMapping
    public Result<CbSeckillActivity> save(@Valid @RequestBody SeckillActivitySaveDTO dto) {
        return Result.success(seckillActivityService.save(dto));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        seckillActivityService.updateStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/preheat")
    public Result<Void> preheat(@PathVariable Long id) {
        seckillActivityService.preheat(id);
        return Result.success();
    }
}
