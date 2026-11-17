package com.crossborder.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategorySaveDTO {
    private Long id;
    @NotBlank
    private String name;
    private Long parentId;
    private Integer sortOrder;
    private String icon;
    private Integer status;
}
