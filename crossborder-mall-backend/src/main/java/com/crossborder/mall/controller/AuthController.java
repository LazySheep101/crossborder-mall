package com.crossborder.mall.controller;

import com.crossborder.mall.common.Result;
import com.crossborder.mall.dto.LoginFormDTO;
import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Tag(name = "认证")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "发送短信验证码（本地仅打日志）")
    @PostMapping("/code")
    public Result<Void> sendCode(@RequestParam String phone) {
        authService.sendCode(phone);
        return Result.success();
    }

    @Operation(summary = "验证码登录，返回 JWT")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginFormDTO form) {
        return Result.success(authService.login(form));
    }

    @Operation(summary = "当前用户")
    @GetMapping("/me")
    public Result<UserDTO> me() {
        return Result.success(authService.me());
    }

    @Operation(summary = "退出并拉黑当前 JWT")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        authService.logout(resolveToken(request));
        return Result.success();
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return bearer;
    }
}
