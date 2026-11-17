package com.crossborder.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "mall")
public class MallProperties {
    /**
     * 上传根目录，对应 application.yml 的 mall.upload-dir。
     */
    private String uploadDir = "./uploads";

    /**
     * 管理员手机号白名单（登录后授予 ROLE_ADMIN）。
     */
    private List<String> adminPhones = new ArrayList<>();
}

