package com.crossborder.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cb_product_sku")
public class CbProductSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private String skuCode;
    private String specDesc;
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
