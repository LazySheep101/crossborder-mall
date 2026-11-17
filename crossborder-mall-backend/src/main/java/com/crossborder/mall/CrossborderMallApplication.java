package com.crossborder.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 跨境商城入口（Boot 3.5 / Java 21）。
 */
@SpringBootApplication
@MapperScan("com.crossborder.mall.mapper")
public class CrossborderMallApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrossborderMallApplication.class, args);
    }
}
