package com.crossborder.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuItemDTO {
    private Long id;
    private String skuCode;
    @NotBlank
    private String specDesc;
    @NotNull
    private BigDecimal price;
    @NotNull
    @Min(0)
    private Integer stock;
    private Integer status = 1;
}
