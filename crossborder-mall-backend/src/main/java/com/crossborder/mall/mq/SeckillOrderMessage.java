package com.crossborder.mall.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillOrderMessage implements Serializable {
    private String orderNo;
    private Long userId;
    private Long activityId;
    private Long addressId;
    /** 消息幂等键 */
    private String requestId;
}
