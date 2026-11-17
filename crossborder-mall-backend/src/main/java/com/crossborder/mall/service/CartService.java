package com.crossborder.mall.service;

import com.crossborder.mall.dto.CartAddDTO;
import com.crossborder.mall.entity.CbCartItem;

import java.util.List;
import java.util.Map;

public interface CartService {
    List<CbCartItem> listMine();

    CbCartItem add(CartAddDTO dto);

    void updateQuantity(Long id, int quantity);

    void updateChecked(Long id, boolean checked);

    void checkAll(boolean checked);

    void remove(Long id);

    void clearChecked();

    Map<String, Object> summary();

    List<CbCartItem> listCheckedFilled(Long userId);
}
