package com.nd.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Direct Exchange 模式配置
 *
 */
@Configuration
public class DirectRabbitConfig {

    public static final String SIMPLE_DIRECT_EXCHANGE_NAME = "simple-direct-exchange";

    public static final String SIMPLE_DIRECT_QUEUE_NAME = "simple-direct-queue";

    public static final String SIMPLE_DIRECT_ROUTING_KEY = "simple-direct-routing-key";

    /**
     * 直连型交换器
     * @return
     */
    @Bean
    public DirectExchange directExchange() {
        // 开启持久化
        return new DirectExchange(SIMPLE_DIRECT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue directQueue() {
        // 开启持久化
        return new Queue(SIMPLE_DIRECT_QUEUE_NAME, true);
    }

    @Bean
    public Binding directBinding() {
        // 队列绑定交换器，并且设置路由键
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(SIMPLE_DIRECT_ROUTING_KEY);
    }

}
