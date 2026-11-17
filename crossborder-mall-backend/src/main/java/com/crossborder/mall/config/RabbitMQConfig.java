package com.crossborder.mall.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀异步落库 + 订单超时关单（TTL + DLX）。
 */
@Configuration
public class RabbitMQConfig {

    @Value("${mall.seckill.exchange}")
    private String seckillExchangeName;

    @Value("${mall.seckill.queue}")
    private String seckillQueueName;

    @Value("${mall.seckill.routing-key}")
    private String seckillRoutingKey;

    @Value("${mall.order.delay-exchange}")
    private String orderDelayExchangeName;

    @Value("${mall.order.process-exchange}")
    private String orderProcessExchangeName;

    @Value("${mall.order.delay-queue}")
    private String orderDelayQueueName;

    @Value("${mall.order.close-queue}")
    private String orderCloseQueueName;

    @Value("${mall.order.delay-routing-key}")
    private String orderDelayRoutingKey;

    @Value("${mall.order.close-routing-key}")
    private String orderCloseRoutingKey;

    @Value("${mall.order.ttl-ms}")
    private long orderTtlMs;

    @Bean
    public DirectExchange seckillExchange() {
        return new DirectExchange(seckillExchangeName, true, false);
    }

    @Bean
    public Queue seckillOrderQueue() {
        return new Queue(seckillQueueName, true);
    }

    @Bean
    public Binding seckillOrderBinding() {
        return BindingBuilder.bind(seckillOrderQueue()).to(seckillExchange()).with(seckillRoutingKey);
    }

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(orderDelayExchangeName, true, false);
    }

    @Bean
    public DirectExchange orderProcessExchange() {
        return new DirectExchange(orderProcessExchangeName, true, false);
    }

    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable(orderDelayQueueName)
                .withArgument("x-message-ttl", orderTtlMs)
                .withArgument("x-dead-letter-exchange", orderProcessExchangeName)
                .withArgument("x-dead-letter-routing-key", orderCloseRoutingKey)
                .build();
    }

    @Bean
    public Queue orderCloseQueue() {
        return QueueBuilder.durable(orderCloseQueueName).build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with(orderDelayRoutingKey);
    }

    @Bean
    public Binding orderCloseBinding() {
        return BindingBuilder.bind(orderCloseQueue()).to(orderProcessExchange()).with(orderCloseRoutingKey);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
