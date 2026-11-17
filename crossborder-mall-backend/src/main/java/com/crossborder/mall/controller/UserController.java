package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.ProfileUpdateDTO;
import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.service.AuthService;
import com.crossborder.mall.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "当前资料")
    @GetMapping("/profile")
    public Result<UserDTO> profile() {
        return Result.success(authService.me());
    }

    @Operation(summary = "更新资料")
    @PutMapping("/profile")
    public Result<UserDTO> updateProfile(@RequestBody ProfileUpdateDTO dto) {
        return Result.success(userService.updateProfile(dto));
    }

    @Operation(summary = "用户基本信息")
    @GetMapping("/{id}")
    public Result<UserDTO> detail(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}
