package com.crossborder.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crossborder.mall.entity.CbProduct;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CbProductMapper extends BaseMapper<CbProduct> {

    @Update("""
            UPDATE cb_product
            SET stock = stock - #{qty}, sales_count = sales_count + #{qty}
            WHERE id = #{id} AND stock >= #{qty}
            """)
    int decreaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Update("""
            UPDATE cb_product
            SET stock = stock + #{qty}, sales_count = GREATEST(sales_count - #{qty}, 0)
            WHERE id = #{id}
            """)
    int increaseStock(@Param("id") Long id, @Param("qty") int qty);
}

