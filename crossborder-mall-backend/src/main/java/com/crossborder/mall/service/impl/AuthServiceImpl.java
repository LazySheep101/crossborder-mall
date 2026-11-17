package com.crossborder.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crossborder.mall.common.ResultCode;
import com.crossborder.mall.dto.LoginFormDTO;
import com.crossborder.mall.dto.UserDTO;
import com.crossborder.mall.entity.User;
import com.crossborder.mall.exception.BusinessException;
import com.crossborder.mall.config.MallProperties;
import com.crossborder.mall.mapper.UserMapper;
import com.crossborder.mall.security.JwtTokenProvider;
import com.crossborder.mall.service.AuthService;
import com.crossborder.mall.utils.RedisConstants;
import com.crossborder.mall.utils.RedisRateLimiter;
import com.crossborder.mall.utils.RegexUtils;
import com.crossborder.mall.utils.SystemConstants;
import com.crossborder.mall.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final MallProperties mallProperties;
    private final RedisRateLimiter redisRateLimiter;

    @Override
    public void sendCode(String phone) {
        if (RegexUtils.isPhoneInvalid(phone)) {
            throw new BusinessException(ResultCode.INVALID_PHONE);
        }
        redisRateLimiter.check("login-code:" + phone, 5, 60);
        String code = RandomUtil.randomNumbers(6);
        stringRedisTemplate.opsForValue().set(
                RedisConstants.LOGIN_CODE_KEY + phone,
                code,
                RedisConstants.LOGIN_CODE_TTL,
                TimeUnit.MINUTES);
        log.debug("发送验证码成功，phone={}, code={}", phone, code);
    }

    @Override
    public Map<String, Object> login(LoginFormDTO form) {
        String phone = form.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            throw new BusinessException(ResultCode.INVALID_PHONE);
        }
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (cacheCode == null || !cacheCode.equals(form.getCode())) {
            throw new BusinessException(ResultCode.INVALID_CODE);
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            user = createUserWithPhone(phone);
        }

        String role = resolveRole(user.getPhone());
        String token = jwtTokenProvider.createToken(user.getId(), user.getNickName(), role);
        UserDTO userDTO = toDto(user, role);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userDTO);
        return data;
    }

    @Override
    public UserDTO me() {
        UserDTO current = UserHolder.getUser();
        if (current == null || current.getId() == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        User user = userMapper.selectById(current.getId());
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toDto(user, resolveRole(user.getPhone()));
    }

    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token) || !jwtTokenProvider.validate(token)) {
            return;
        }
        Date exp = jwtTokenProvider.getExpiration(token);
        long ttl = exp.getTime() - System.currentTimeMillis();
        if (ttl <= 0) {
            return;
        }
        stringRedisTemplate.opsForValue().set(
                RedisConstants.JWT_BLACKLIST_KEY + token,
                "1",
                ttl,
                TimeUnit.MILLISECONDS);
    }

    private String resolveRole(String phone) {
        if (phone != null && mallProperties.getAdminPhones() != null
                && mallProperties.getAdminPhones().contains(phone)) {
            return "ADMIN";
        }
        return "USER";
    }

    private UserDTO toDto(User user, String role) {
        UserDTO dto = BeanUtil.copyProperties(user, UserDTO.class);
        dto.setRole(role);
        return dto;
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        userMapper.insert(user);
        return user;
    }
}
