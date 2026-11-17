package com.crossborder.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.config.MallProperties;
import com.crossborder.mall.dto.ProfileUpdateDTO;
import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.entity.User;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.mapper.UserMapper;
import com.crossborder.mall.service.UserService;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final MallProperties mallProperties;

    @Override
    public UserDTO getById(Long id) {
        if (id == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户 id 不能为空");
        }
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toDto(user);
    }

    @Override
    public UserDTO updateProfile(ProfileUpdateDTO dto) {
        Long userId = UserHolder.requireUser().getId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (StringUtils.hasText(dto.getNickName())) {
            user.setNickName(dto.getNickName().trim());
        }
        if (dto.getIcon() != null) {
            user.setIcon(dto.getIcon());
        }
        userMapper.updateById(user);
        return toDto(user);
    }

    private UserDTO toDto(User user) {
        UserDTO dto = BeanUtil.copyProperties(user, UserDTO.class);
        boolean admin = user.getPhone() != null
                && mallProperties.getAdminPhones() != null
                && mallProperties.getAdminPhones().contains(user.getPhone());
        dto.setRole(admin ? "ADMIN" : "USER");
        return dto;
    }
}
