package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.service.MallOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MallOrderService mallOrderService;

    @GetMapping
    public Result<Map<String, Object>> dashboard() {
        return Result.success(mallOrderService.adminDashboard());
    }
}
