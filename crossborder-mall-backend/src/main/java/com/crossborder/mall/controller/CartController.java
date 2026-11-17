package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.CartAddDTO;
import com.crossborder.mall.entity.CbCartItem;
import com.crossborder.mall.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<List<CbCartItem>> list() {
        return Result.success(cartService.listMine());
    }

    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        return Result.success(cartService.summary());
    }

    @PostMapping
    public Result<CbCartItem> add(@Valid @RequestBody CartAddDTO dto) {
        return Result.success(cartService.add(dto));
    }

    @PutMapping("/{id}/quantity")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam int quantity) {
        cartService.updateQuantity(id, quantity);
        return Result.success();
    }

    @PutMapping("/{id}/checked")
    public Result<Void> updateChecked(@PathVariable Long id, @RequestParam boolean checked) {
        cartService.updateChecked(id, checked);
        return Result.success();
    }

    @PutMapping("/checked")
    public Result<Void> checkAll(@RequestParam boolean checked) {
        cartService.checkAll(checked);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(id);
        return Result.success();
    }
}
