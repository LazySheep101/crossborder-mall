package com.crossborder.mall.utils;

import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.exception.BusinessException;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static UserDTO requireUser() {
        UserDTO user = tl.get();
        if (user == null || user.getId() == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return user;
    }

    public static void removeUser(){
        tl.remove();
    }
}
