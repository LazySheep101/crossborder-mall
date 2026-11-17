package com.crossborder.mall.mq;

import com.crossborder.mall.service.MallOrderService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCloseConsumer {

    private final MallOrderService mallOrderService;

    @RabbitListener(queues = "${mall.order.close-queue}")
    public void onMessage(OrderCloseMessage body, Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            if (body != null && body.getOrderNo() != null) {
                mallOrderService.closeIfUnpaid(body.getOrderNo());
            }
            channel.basicAck(tag, false);
        } catch (Exception e) {
            log.error("订单超时关单失败 orderNo={}", body == null ? null : body.getOrderNo(), e);
            channel.basicNack(tag, false, false);
        }
    }
}
