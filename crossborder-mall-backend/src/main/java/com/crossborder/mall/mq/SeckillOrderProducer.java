package com.crossborder.mall.mq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeckillOrderProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mall.seckill.exchange}")
    private String exchange;

    @Value("${mall.seckill.routing-key}")
    private String routingKey;

    public void send(SeckillOrderMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
