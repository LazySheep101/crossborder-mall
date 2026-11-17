package com.crossborder.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cb_seckill_activity")
public class CbSeckillActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long productId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer stock;
    private Integer sold;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String productName;
    @TableField(exist = false)
    private String productImage;
    @TableField(exist = false)
    private String specDesc;
    @TableField(exist = false)
    private BigDecimal originPrice;
}
