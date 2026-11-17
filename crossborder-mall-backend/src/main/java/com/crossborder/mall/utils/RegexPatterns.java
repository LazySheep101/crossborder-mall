package com.crossborder.mall.utils;

/**
 */
public abstract class RegexPatterns {
    /**
     * 手机号正则（中国大陆 11 位，第二位 3-9，覆盖现行号段含 5G/广电/虚拟运营商）
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    public static final String PASSWORD_REGEX = "^[A-Za-z0-9_]{4,32}$";
    /**
     * 验证码正则, 6位数字或字母
     */
    public static final String VERIFY_CODE_REGEX = "^[A-Za-z\\d]{6}$";

}
