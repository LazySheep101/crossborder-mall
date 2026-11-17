package com.crossborder.mall.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateDTO {
    @NotNull
    private Long addressId;
    private String remark;
}
