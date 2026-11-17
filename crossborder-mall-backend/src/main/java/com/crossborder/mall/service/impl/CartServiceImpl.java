package com.crossborder.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.CartAddDTO;
import com.crossborder.mall.entity.CbCartItem;
import com.crossborder.mall.entity.CbProduct;
import com.crossborder.mall.entity.CbProductSku;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbCartItemMapper;
import com.crossborder.mall.mapper.CbProductMapper;
import com.crossborder.mall.mapper.CbProductSkuMapper;
import com.crossborder.mall.service.CartService;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CbCartItemMapper cartItemMapper;
    private final CbProductMapper productMapper;
    private final CbProductSkuMapper skuMapper;

    @Override
    public List<CbCartItem> listMine() {
        Long userId = UserHolder.requireUser().getId();
        List<CbCartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CbCartItem>()
                .eq(CbCartItem::getUserId, userId)
                .orderByDesc(CbCartItem::getId));
        items.forEach(this::fillDisplay);
        return items;
    }

    @Override
    @Transactional
    public CbCartItem add(CartAddDTO dto) {
        Long userId = UserHolder.requireUser().getId();
        long skuId = dto.getSkuId() == null ? 0L : dto.getSkuId();
        Sellable sellable = resolveSellable(dto.getProductId(), skuId);
        if (dto.getQuantity() > sellable.stock()) {
            throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY, "库存不足");
        }

        CbCartItem existing = cartItemMapper.selectOne(new LambdaQueryWrapper<CbCartItem>()
                .eq(CbCartItem::getUserId, userId)
                .eq(CbCartItem::getProductId, dto.getProductId())
                .eq(CbCartItem::getSkuId, skuId)
                .last("LIMIT 1"));
        if (existing != null) {
            int next = existing.getQuantity() + dto.getQuantity();
            if (next > sellable.stock()) {
                throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY, "库存不足");
            }
            existing.setQuantity(next);
            existing.setChecked(1);
            cartItemMapper.updateById(existing);
            fillDisplay(existing);
            return existing;
        }

        CbCartItem item = new CbCartItem();
        item.setUserId(userId);
        item.setProductId(dto.getProductId());
        item.setSkuId(skuId);
        item.setQuantity(dto.getQuantity());
        item.setChecked(1);
        cartItemMapper.insert(item);
        fillDisplay(item);
        return item;
    }

    @Override
    public void updateQuantity(Long id, int quantity) {
        if (quantity < 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "数量至少为 1");
        }
        CbCartItem item = requireOwned(id);
        Sellable sellable = resolveSellable(item.getProductId(), item.getSkuId());
        if (quantity > sellable.stock()) {
            throw new BusinessException(ResultCode.SECKILL_STOCK_EMPTY, "库存不足");
        }
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void updateChecked(Long id, boolean checked) {
        CbCartItem item = requireOwned(id);
        item.setChecked(checked ? 1 : 0);
        cartItemMapper.updateById(item);
    }

    @Override
    public void checkAll(boolean checked) {
        Long userId = UserHolder.requireUser().getId();
        cartItemMapper.update(null, new LambdaUpdateWrapper<CbCartItem>()
                .eq(CbCartItem::getUserId, userId)
                .set(CbCartItem::getChecked, checked ? 1 : 0));
    }

    @Override
    public void remove(Long id) {
        CbCartItem item = requireOwned(id);
        cartItemMapper.deleteById(item.getId());
    }

    @Override
    public void clearChecked() {
        Long userId = UserHolder.requireUser().getId();
        cartItemMapper.delete(new LambdaQueryWrapper<CbCartItem>()
                .eq(CbCartItem::getUserId, userId)
                .eq(CbCartItem::getChecked, 1));
    }

    @Override
    public Map<String, Object> summary() {
        List<CbCartItem> items = listMine();
        int count = items.stream().mapToInt(CbCartItem::getQuantity).sum();
        BigDecimal checkedAmount = items.stream()
                .filter(i -> i.getChecked() != null && i.getChecked() == 1)
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("checkedAmount", checkedAmount);
        map.put("checkedCount", items.stream()
                .filter(i -> i.getChecked() != null && i.getChecked() == 1)
                .mapToInt(CbCartItem::getQuantity)
                .sum());
        return map;
    }

    @Override
    public List<CbCartItem> listCheckedFilled(Long userId) {
        List<CbCartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CbCartItem>()
                .eq(CbCartItem::getUserId, userId)
                .eq(CbCartItem::getChecked, 1));
        items.forEach(this::fillDisplay);
        return items;
    }

    private CbCartItem requireOwned(Long id) {
        Long userId = UserHolder.requireUser().getId();
        CbCartItem item = cartItemMapper.selectById(id);
        if (item == null || !userId.equals(item.getUserId())) {
            throw new BusinessException(ResultCode.NOT_FOUND, "购物车项不存在");
        }
        return item;
    }

    private void fillDisplay(CbCartItem item) {
        Sellable sellable = resolveSellable(item.getProductId(), item.getSkuId() == null ? 0L : item.getSkuId());
        item.setProductName(sellable.productName());
        item.setProductImage(sellable.image());
        item.setSpecDesc(sellable.specDesc());
        item.setUnitPrice(sellable.price());
        item.setStock(sellable.stock());
    }

    private Sellable resolveSellable(Long productId, long skuId) {
        CbProduct product = productMapper.selectById(productId);
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在或已下架");
        }
        if (product.getHasSku() != null && product.getHasSku() == 1) {
            if (skuId <= 0) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "请选择规格");
            }
            CbProductSku sku = skuMapper.selectById(skuId);
            if (sku == null || !productId.equals(sku.getProductId())
                    || sku.getStatus() == null || sku.getStatus() != 1) {
                throw new BusinessException(ResultCode.NOT_FOUND, "规格不存在");
            }
            return new Sellable(product.getName(), product.getMainImage(), sku.getSpecDesc(),
                    sku.getPrice(), sku.getStock());
        }
        return new Sellable(product.getName(), product.getMainImage(), "",
                product.getPrice(), product.getStock());
    }

    private record Sellable(String productName, String image, String specDesc,
                            BigDecimal price, Integer stock) {
    }
}
