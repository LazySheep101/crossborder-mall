package com.crossborder.mall.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UploadResourceConfig implements WebMvcConfigurer {

    private final MallProperties mallProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(mallProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
        } catch (IOException ex) {
            log.warn("创建上传目录失败: {}", dir, ex);
        }
        String location = dir.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        // 优先本地上传目录，其次 classpath 内置商品图
        registry.addResourceHandler("/imgs/**")
                .addResourceLocations(location, "classpath:/static/imgs/");
    }
}

