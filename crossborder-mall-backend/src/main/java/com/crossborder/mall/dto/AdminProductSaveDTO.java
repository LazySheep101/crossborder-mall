package com.crossborder.mall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdminProductSaveDTO {
    private Long id;
    @NotNull
    private Long categoryId;
    @NotBlank
    private String name;
    private String subtitle;
    private String mainImage;
    private String detail;
    private String originCountry;
    @NotNull
    private BigDecimal price;
    private Integer stock;
    private Integer status;
    private Integer hasSku;
}
