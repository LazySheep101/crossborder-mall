package com.crossborder.mall.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressSaveDTO {
    @NotBlank
    private String receiverName;
    @NotBlank
    private String receiverPhone;
    private String province;
    private String city;
    private String district;
    @NotBlank
    private String detail;
    private Boolean isDefault;
}
