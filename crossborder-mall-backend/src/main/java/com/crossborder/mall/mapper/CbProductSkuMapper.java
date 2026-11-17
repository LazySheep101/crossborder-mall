package com.crossborder.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.mall.entity.CbProductSku;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CbProductSkuMapper extends BaseMapper<CbProductSku> {

    @Update("""
            UPDATE cb_product_sku
            SET stock = stock - #{qty}
            WHERE id = #{id} AND stock >= #{qty}
            """)
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Update("""
            UPDATE cb_product_sku
            SET stock = stock + #{qty}
            WHERE id = #{id}
            """)
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);
}

