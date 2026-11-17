package com.crossborder.mall.service;

import com.crossborder.mall.dto.SeckillActivitySaveDTO;
import com.crossborder.mall.entity.CbSeckillActivity;

import java.util.List;
import java.util.Map;

public interface SeckillActivityService {
    List<CbSeckillActivity> listOnline();

    CbSeckillActivity getDetail(Long id);

    Map<String, Object> placeOrder(Long activityId, Long addressId);

    Map<String, Object> result(Long activityId);

    List<CbSeckillActivity> adminList();

    CbSeckillActivity save(SeckillActivitySaveDTO dto);

    void updateStatus(Long id, int status);

    void preheat(Long id);
}
