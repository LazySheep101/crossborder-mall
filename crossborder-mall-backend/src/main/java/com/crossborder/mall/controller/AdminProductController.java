package com.crossborder.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.Result;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.AdminProductSaveDTO;
import com.crossborder.mall.dto.SkuItemDTO;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.entity.CbProductSku;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.service.ProductCatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;
    private final ProductCatalogService productCatalogService;

    @GetMapping
    public Result<Page<CbProduct>> page(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false) Integer status,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        Page<CbProduct> p = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50));
        return Result.success(productMapper.selectPage(p, new LambdaQueryWrapper<CbProduct>()
                .eq(status != null, CbProduct::getStatus, status)
                .like(StringUtils.hasText(keyword), CbProduct::getName, keyword)
                .orderByDesc(CbProduct::getId)));
    }

    @GetMapping("/{id}")
    public Result<CbProduct> detail(@PathVariable Long id) {
        CbProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        product.setSkus(skuMapper.selectList(new LambdaQueryWrapper<CbProductSku>()
                .eq(CbProductSku::getProductId, id)
                .orderByAsc(CbProductSku::getId)));
        return Result.success(product);
    }

    @PostMapping
    public Result<CbProduct> save(@Valid @RequestBody AdminProductSaveDTO dto) {
        CbProduct product = dto.getId() == null ? new CbProduct() : productMapper.selectById(dto.getId());
        if (dto.getId() != null && product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setSubtitle(dto.getSubtitle());
        product.setMainImage(dto.getMainImage());
        product.setDetail(dto.getDetail());
        product.setOriginCountry(dto.getOriginCountry());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock() == null ? 0 : dto.getStock());
        product.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        product.setHasSku(dto.getHasSku() == null ? 0 : dto.getHasSku());
        if (product.getSalesCount() == null) {
            product.setSalesCount(0);
        }
        if (dto.getId() == null) {
            productMapper.insert(product);
        } else {
            productMapper.updateById(product);
        }
        productCatalogService.evictProduct(product.getId());
        return Result.success(product);
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam int status) {
        CbProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        productCatalogService.evictProduct(id);
        return Result.success();
    }

    @GetMapping("/{id}/skus")
    public Result<List<CbProductSku>> listSkus(@PathVariable Long id) {
        requireProduct(id);
        return Result.success(skuMapper.selectList(new LambdaQueryWrapper<CbProductSku>()
                .eq(CbProductSku::getProductId, id)
                .orderByAsc(CbProductSku::getId)));
    }

    @PutMapping("/{id}/skus")
    @Transactional
    public Result<List<CbProductSku>> saveSkus(@PathVariable Long id,
                                               @Valid @RequestBody List<SkuItemDTO> items) {
        CbProduct product = requireProduct(id);
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "至少保留一个 SKU");
        }
        skuMapper.delete(new LambdaQueryWrapper<CbProductSku>().eq(CbProductSku::getProductId, id));
        BigDecimal minPrice = null;
        for (SkuItemDTO item : items) {
            CbProductSku sku = new CbProductSku();
            sku.setProductId(id);
            sku.setSkuCode(item.getSkuCode());
            sku.setSpecDesc(item.getSpecDesc());
            sku.setPrice(item.getPrice());
            sku.setStock(item.getStock());
            sku.setStatus(item.getStatus() == null ? 1 : item.getStatus());
            skuMapper.insert(sku);
            if (minPrice == null || item.getPrice().compareTo(minPrice) < 0) {
                minPrice = item.getPrice();
            }
        }
        product.setHasSku(1);
        product.setPrice(minPrice);
        product.setStock(0);
        productMapper.updateById(product);
        productCatalogService.evictProduct(id);
        return listSkus(id);
    }

    @DeleteMapping("/{id}/skus")
    @Transactional
    public Result<Void> clearSkus(@PathVariable Long id) {
        CbProduct product = requireProduct(id);
        skuMapper.delete(new LambdaQueryWrapper<CbProductSku>().eq(CbProductSku::getProductId, id));
        product.setHasSku(0);
        productMapper.updateById(product);
        productCatalogService.evictProduct(id);
        return Result.success();
    }

    private CbProduct requireProduct(Long id) {
        CbProduct product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        return product;
    }
}
