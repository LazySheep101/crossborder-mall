package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.entity.CbCategory;
import com.crossborder.mall.service.ProductCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "商品分类")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ProductCatalogService productCatalogService;

    @Operation(summary = "分类列表")
    @GetMapping
    public Result<List<CbCategory>> list() {
        return Result.success(productCatalogService.listCategories());
    }

    @Operation(summary = "分类树")
    @GetMapping("/tree")
    public Result<List<CbCategory>> tree() {
        return Result.success(productCatalogService.listCategoryTree());
    }
}
