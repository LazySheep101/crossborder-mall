package com.crossborder.mall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartAddDTO {
    @NotNull
    private Long productId;
    private Long skuId;
    @NotNull
    @Min(1)
    private Integer quantity;
}
