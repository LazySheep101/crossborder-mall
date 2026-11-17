package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.config.MallProperties;
import com.crossborder.mall.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Tag(name = "上传")
@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class UploadController {

    private final MallProperties mallProperties;

    @Operation(summary = "上传笔记图片，返回 /blogs/... 相对路径")
    @PostMapping("/blog")
    public Result<String> uploadBlog(@RequestParam("file") MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }
        String relative = createRelativeName(image.getOriginalFilename());
        Path target = resolveUnderUpload(relative);
        try {
            Files.createDirectories(target.getParent());
            image.transferTo(target);
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "文件上传失败");
        }
        log.debug("文件上传成功，{}", relative);
        return Result.success(relative);
    }

    @Operation(summary = "删除已上传的笔记图片")
    @DeleteMapping("/blog")
    public Result<Void> deleteBlog(@RequestParam("name") String filename) {
        Path target = resolveUnderUpload(filename);
        if (Files.isDirectory(target)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "错误的文件名称");
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "文件删除失败");
        }
        return Result.success();
    }

    private String createRelativeName(String originalFilename) {
        String suffix = "jpg";
        if (StringUtils.hasText(originalFilename) && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
        }
        suffix = suffix.replaceAll("[^A-Za-z0-9]", "");
        if (suffix.isEmpty() || suffix.length() > 8) {
            suffix = "jpg";
        }
        String name = UUID.randomUUID().toString();
        int hash = name.hashCode();
        int d1 = hash & 0xF;
        int d2 = (hash >> 4) & 0xF;
        return "/blogs/" + d1 + "/" + d2 + "/" + name + "." + suffix;
    }

    private Path resolveUnderUpload(String filename) {
        if (!StringUtils.hasText(filename) || filename.contains("..")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "错误的文件名称");
        }
        String relative = filename.replace('\\', '/');
        if (relative.startsWith("/imgs/")) {
            relative = relative.substring("/imgs".length());
        }
        while (relative.startsWith("/")) {
            relative = relative.substring(1);
        }
        if (!StringUtils.hasText(relative)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "错误的文件名称");
        }
        Path root = Paths.get(mallProperties.getUploadDir()).toAbsolutePath().normalize();
        Path target = root.resolve(relative).normalize();
        if (!target.startsWith(root)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "错误的文件名称");
        }
        return target;
    }
}
