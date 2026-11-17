package com.crossborder.mall.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nickName;
    private String icon;
    private String phone;
    /** USER / ADMIN */
    private String role;
}

