package com.crossborder.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.entity.CbCategory;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.entity.CbProductSku;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbCategoryMapper;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.service.ProductCatalogService;
import com.crossborder.mall.utils.CacheClient;
import com.crossborder.mall.utils.RedisConstants;
import com.crossborder.mall.utils.RedisData;
import com.crossborder.mall.utils.SimpleRedisLock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductCatalogServiceImpl implements ProductCatalogService {

    private static final ExecutorService CACHE_REBUILD = Executors.newFixedThreadPool(4);

    private final CbCategoryMapper categoryMapper;
    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;
    private final CacheClient cacheClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public List<CbCategory> listCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<CbCategory>()
                .eq(CbCategory::getStatus, 1)
                .orderByAsc(CbCategory::getSortOrder)
                .orderByAsc(CbCategory::getId));
    }

    @Override
    public List<CbCategory> listCategoryTree() {
        List<CbCategory> all = listCategories();
        Map<Long, CbCategory> byId = new LinkedHashMap<>();
        for (CbCategory category : all) {
            category.setChildren(new ArrayList<>());
            byId.put(category.getId(), category);
        }
        List<CbCategory> roots = new ArrayList<>();
        for (CbCategory category : all) {
            Long parentId = category.getParentId();
            CbCategory parent = parentId == null ? null : byId.get(parentId);
            if (parent == null || parentId == 0L) {
                roots.add(category);
            } else {
                parent.getChildren().add(category);
            }
        }
        return roots;
    }

    @Override
    public Page<CbProduct> pageProducts(Long categoryId, String keyword, String sortBy, int page, int size) {
        Page<CbProduct> p = new Page<>(Math.max(page, 1), Math.min(Math.max(size, 1), 50));
        LambdaQueryWrapper<CbProduct> qw = new LambdaQueryWrapper<CbProduct>()
                .eq(CbProduct::getStatus, 1)
                .eq(categoryId != null && categoryId > 0, CbProduct::getCategoryId, categoryId)
                .like(StringUtils.hasText(keyword), CbProduct::getName, keyword);
        applySort(qw, sortBy);
        return productMapper.selectPage(p, qw);
    }

    private void applySort(LambdaQueryWrapper<CbProduct> qw, String sortBy) {
        if (!StringUtils.hasText(sortBy)) {
            qw.orderByDesc(CbProduct::getSalesCount);
            return;
        }
        switch (sortBy) {
            case "price_asc" -> qw.orderByAsc(CbProduct::getPrice);
            case "price_desc" -> qw.orderByDesc(CbProduct::getPrice);
            case "newest" -> qw.orderByDesc(CbProduct::getId);
            default -> qw.orderByDesc(CbProduct::getSalesCount);
        }
    }

    @Override
    public CbProduct getDetail(Long id) {
        String key = RedisConstants.CACHE_PRODUCT_KEY + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json != null && json.isEmpty()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }
        if (StrUtil.isNotBlank(json)) {
            RedisData redisData = JSONUtil.toBean(json, RedisData.class);
            CbProduct cached = JSONUtil.toBean(JSONUtil.toJsonStr(redisData.getData()), CbProduct.class);
            if (redisData.getExpireTime() != null && redisData.getExpireTime().isAfter(LocalDateTime.now())) {
                return cached;
            }
            SimpleRedisLock lock = new SimpleRedisLock("product:" + id, stringRedisTemplate);
            if (lock.tryLock(10)) {
                CACHE_REBUILD.submit(() -> {
                    try {
                        CbProduct fresh = loadOnShelf(id);
                        if (fresh == null) {
                            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
                        } else {
                            cacheClient.setWithLogicalExpire(key, fresh, RedisConstants.CACHE_PRODUCT_TTL, TimeUnit.SECONDS);
                        }
                    } finally {
                        lock.unlock();
                    }
                });
            }
            return cached;
        }
        CbProduct product = loadOnShelf(id);
        if (product == null) {
            stringRedisTemplate.opsForValue().set(key, "", RedisConstants.CACHE_NULL_TTL, TimeUnit.MINUTES);
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }
        cacheClient.setWithLogicalExpire(key, product, RedisConstants.CACHE_PRODUCT_TTL, TimeUnit.SECONDS);
        return product;
    }

    @Override
    public void evictProduct(Long id) {
        if (id == null) {
            return;
        }
        stringRedisTemplate.delete(RedisConstants.CACHE_PRODUCT_KEY + id);
    }

    private CbProduct loadOnShelf(Long id) {
        CbProduct product = productMapper.selectById(id);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            return null;
        }
        if (product.getHasSku() != null && product.getHasSku() == 1) {
            List<CbProductSku> skus = skuMapper.selectList(new LambdaQueryWrapper<CbProductSku>()
                    .eq(CbProductSku::getProductId, id)
                    .eq(CbProductSku::getStatus, 1));
            product.setSkus(skus);
        }
        return product;
    }
}
