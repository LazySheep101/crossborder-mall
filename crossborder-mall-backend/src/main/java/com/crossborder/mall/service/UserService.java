package com.crossborder.mall.service;

import com.crossborder.mall.dto.ProfileUpdateDTO;
import com.crossborder.mall.dto.UserDTO;

public interface UserService {
    UserDTO getById(Long id);

    UserDTO updateProfile(ProfileUpdateDTO dto);
}
