package com.crossborder.mall.mq;

import com.crossborder.mall.service.impl.SeckillOrderPersistService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderConsumer {

    private final SeckillOrderPersistService seckillOrderPersistService;

    @RabbitListener(queues = "${mall.seckill.queue}")
    public void onMessage(SeckillOrderMessage payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            boolean ok = seckillOrderPersistService.persist(payload);
            if (!ok) {
                seckillOrderPersistService.compensateRedis(payload);
            }
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("秒杀落库失败 orderNo={}", payload == null ? null : payload.getOrderNo(), ex);
            if (payload != null) {
                seckillOrderPersistService.compensateRedis(payload);
            }
            channel.basicNack(tag, false, false);
        }
    }
}
