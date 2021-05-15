package com.nd.config;

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


}
