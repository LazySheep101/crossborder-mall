package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.AddressSaveDTO;
import com.crossborder.mall.entity.CbAddress;
import com.crossborder.mall.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<List<CbAddress>> list() {
        return Result.success(addressService.listMine());
    }

    @GetMapping("/default")
    public Result<CbAddress> getDefault() {
        return Result.success(addressService.getDefault());
    }

    @PostMapping
    public Result<CbAddress> create(@Valid @RequestBody AddressSaveDTO dto) {
        return Result.success(addressService.create(dto));
    }

    @PutMapping("/{id}")
    public Result<CbAddress> update(@PathVariable Long id, @Valid @RequestBody AddressSaveDTO dto) {
        return Result.success(addressService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return Result.success();
    }
}
