package com.crossborder.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.mall.entity.CbSeckillActivity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CbSeckillActivityMapper extends BaseMapper<CbSeckillActivity> {

    @Update("""
            UPDATE cb_seckill_activity
            SET stock = stock - 1, sold = sold + 1
            WHERE id = #{id} AND stock > 0 AND status = 1
            """)
    int decreaseStock(@Param("id") Long id);

    @Update("""
            UPDATE cb_seckill_activity
            SET stock = stock + #{qty}, sold = GREATEST(sold - #{qty}, 0)
            WHERE id = #{id}
            """)
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);
}
