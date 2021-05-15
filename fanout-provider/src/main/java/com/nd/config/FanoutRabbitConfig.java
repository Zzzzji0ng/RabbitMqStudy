package com.nd.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * FANOUT 模式配置
 *
 */
@Configuration
public class FanoutRabbitConfig {

    public static final String SIMPLE_FANOUT_EXCHANGE_NAME = "simple-fanout-exchange";

    public static final String SIMPLE_FANOUT_QUEUE_NAME1 = "simple-fanout-queue1";

    public static final String SIMPLE_FANOUT_QUEUE_NAME2 = "simple-fanout-queue2";

    public static final String SIMPLE_FANOUT_QUEUE_NAME3 = "simple-fanout-queue3";

    /**
     * 广播型交换器
     * @return
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        // 开启持久化
        return new FanoutExchange(SIMPLE_FANOUT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue fanoutQueue1() {
        // 开启持久化
        return new Queue(SIMPLE_FANOUT_QUEUE_NAME1, true);
    }

    @Bean
    public Queue fanoutQueue2() {
        // 开启持久化
        return new Queue(SIMPLE_FANOUT_QUEUE_NAME2, true);
    }

    @Bean
    public Queue fanoutQueue3() {
        // 开启持久化
        return new Queue(SIMPLE_FANOUT_QUEUE_NAME3, true);
    }

    @Bean
    public Binding fanoutBinding1() {
        // 队列绑定交换器，无需配置路由键，配置了也没用
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        // 队列绑定交换器
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding3() {
        // 队列绑定交换器
        return BindingBuilder.bind(fanoutQueue3()).to(fanoutExchange());
    }
}
