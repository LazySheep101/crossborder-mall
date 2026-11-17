package com.crossborder.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SeckillActivitySaveDTO {
    private Long id;
    @NotBlank
    private String title;
    @NotNull
    private Long productId;
    private Long skuId;
    @NotNull
    private BigDecimal seckillPrice;
    @NotNull
    private Integer stock;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    private Integer status;
}
