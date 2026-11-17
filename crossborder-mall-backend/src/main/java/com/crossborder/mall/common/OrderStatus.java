package com.crossborder.mall.common;

import com.crossborder.mall.exception.BusinessException;

import java.util.Map;
import java.util.Set;

public final class OrderStatus {
    public static final String UNPAID = "UNPAID";
    public static final String PAID = "PAID";
    public static final String SHIPPED = "SHIPPED";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";

    private static final Map<String, Set<String>> TRANSITIONS = Map.of(
            UNPAID, Set.of(PAID, CANCELLED),
            PAID, Set.of(SHIPPED, CANCELLED),
            SHIPPED, Set.of(COMPLETED),
            COMPLETED, Set.of(),
            CANCELLED, Set.of()
    );

    private OrderStatus() {
    }

    public static void assertTransit(String from, String to) {
        Set<String> allowed = TRANSITIONS.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new BusinessException(ResultCode.BAD_REQUEST,
                    "订单状态不可从 " + from + " 变更为 " + to);
        }
    }

    public static String label(String status) {
        return switch (status) {
            case UNPAID -> "待支付";
            case PAID -> "已支付";
            case SHIPPED -> "已发货";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
            default -> status;
        };
    }
}
