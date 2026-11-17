package com.crossborder.mall.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCloseProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mall.order.delay-exchange}")
    private String delayExchange;

    @Value("${mall.order.delay-routing-key}")
    private String delayRoutingKey;

    public void sendCloseDelay(String orderNo) {
        rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, new OrderCloseMessage(orderNo));
    }
}
