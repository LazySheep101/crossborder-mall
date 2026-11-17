package com.crossborder.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.dto.OrderCreateDTO;
import com.crossborder.mall.entity.CbOrder;

public interface MallOrderService {
    CbOrder createFromCart(OrderCreateDTO dto);

    Page<CbOrder> pageMine(String status, int page, int size);

    CbOrder getMine(String orderNo);

    void cancelMine(String orderNo, String reason);

    void confirmMine(String orderNo);

    void closeIfUnpaid(String orderNo);

    void markPaid(String orderNo);

    void ship(String orderNo);

    Page<CbOrder> pageAdmin(String status, int page, int size);

    CbOrder getAdmin(String orderNo);

    void cancelAdmin(String orderNo, String reason);

    java.util.Map<String, Object> adminDashboard();
}
