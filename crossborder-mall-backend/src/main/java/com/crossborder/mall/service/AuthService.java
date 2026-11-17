package com.crossborder.mall.service;

import com.crossborder.mall.dto.LoginFormDTO;
import com.crossborder.mall.dto.UserDTO;

import java.util.Map;

public interface AuthService {
    void sendCode(String phone);

    Map<String, Object> login(LoginFormDTO form);

    UserDTO me();

    void logout(String token);
}
