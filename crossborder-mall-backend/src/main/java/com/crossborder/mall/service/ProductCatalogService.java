package com.crossborder.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crossborder.mall.entity.CbCategory;
import com.crossborder.mall.entity.CbProduct;

import java.util.List;

public interface ProductCatalogService {
    List<CbCategory> listCategories();

    List<CbCategory> listCategoryTree();

    Page<CbProduct> pageProducts(Long categoryId, String keyword, String sortBy, int page, int size);

    CbProduct getDetail(Long id);

    void evictProduct(Long id);
}
