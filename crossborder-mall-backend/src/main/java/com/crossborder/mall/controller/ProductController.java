package com.crossborder.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.Result;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.service.ProductCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "商品")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCatalogService productCatalogService;

    @Operation(summary = "商品分页列表")
    @GetMapping
    public Result<Page<CbProduct>> page(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productCatalogService.pageProducts(categoryId, keyword, sortBy, page, size));
    }

    @Operation(summary = "按关键词搜索商品")
    @GetMapping("/search")
    public Result<Page<CbProduct>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(productCatalogService.pageProducts(categoryId, keyword, sortBy, page, size));
    }

    @Operation(summary = "商品详情（含SKU，逻辑过期缓存）")
    @GetMapping("/{id}")
    public Result<CbProduct> detail(@PathVariable Long id) {
        return Result.success(productCatalogService.getDetail(id));
    }
}
