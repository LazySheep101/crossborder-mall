package com.crossborder.mall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crossborder.mall.common.Result;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.CategorySaveDTO;
import com.crossborder.mall.entity.CbCategory;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.CbCategoryMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CbCategoryMapper categoryMapper;

    @GetMapping
    public Result<List<CbCategory>> list() {
        return Result.success(categoryMapper.selectList(new LambdaQueryWrapper<CbCategory>()
                .orderByAsc(CbCategory::getSortOrder)
                .orderByAsc(CbCategory::getId)));
    }

    @PostMapping
    public Result<CbCategory> save(@Valid @RequestBody CategorySaveDTO dto) {
        CbCategory category = dto.getId() == null ? new CbCategory() : categoryMapper.selectById(dto.getId());
        if (dto.getId() != null && category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        category.setName(dto.getName());
        category.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        category.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        category.setIcon(dto.getIcon());
        category.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        if (dto.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.updateById(category);
        }
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
