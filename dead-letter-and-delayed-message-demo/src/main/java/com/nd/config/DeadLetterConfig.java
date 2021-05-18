package com.nd.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class DeadLetterConfig {

    // 业务队列交换器名
    public static final String DELETE_LETTER_BUSINESS_EXCHANGE_NAME = "delete_letter_business_exchange_name";

    // 业务队列名1
    public static final String DELETE_LETTER_BUSINESS_QUEUE_NAME_1 = "delete_letter_business_queue_name_1";

    // 业务队列名2
    public static final String DELETE_LETTER_BUSINESS_QUEUE_NAME_2 = "delete_letter_business_queue_name_2";

    // 业务队列名3
    public static final String DELETE_LETTER_BUSINESS_QUEUE_NAME_3 = "delete_letter_business_queue_name_3";

    // 业务路由键1
    public static final String DELETE_LETTER_BUSINESS_ROUTING_KEY_1 = "delete_letter_business_routing_key_1";

    // 业务路由键2
    public static final String DELETE_LETTER_BUSINESS_ROUTING_KEY_2 = "delete_letter_business_routing_key_2";

    // 业务路由键3
    public static final String DELETE_LETTER_BUSINESS_ROUTING_KEY_3 = "delete_letter_business_routing_key_3";



    // 死信队列交换器名
    public static final String DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME = "delete_letter_dead_letter_exchange_name";

    // 死信队列名1
    public static final String DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_1 = "delete_letter_dead_letter_queue_name_1";

    // 死信队列名2
    public static final String DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_2 = "delete_letter_dead_letter_queue_name_2";

    // 死信队列名3
    public static final String DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_3 = "delete_letter_dead_letter_queue_name_3";

    // 死信路由键1
    public static final String DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_1 = "delete_letter_dead_letter_routing_key_1";

    // 死信路由键2
    public static final String DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_2 = "delete_letter_dead_letter_routing_key_2";

    // 死信路由键3
    public static final String DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_3 = "delete_letter_dead_letter_routing_key_3";




    @Bean("deadLetterBusinessExchange")
    public DirectExchange deadLetterBusinessExchange() {
        return new DirectExchange(DELETE_LETTER_BUSINESS_EXCHANGE_NAME);
    }

    @Bean("deadLetterBusinessQueue1")
    public Queue deadLetterBusinessQueue1() {
        // return new Queue(DELETE_LETTER_BUSINESS_QUEUE_NAME_1);
        // 需要使用这种方式，创建队列。在创建的时候指定他的绑定的死信队列交换器和路由键
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_1);
        return QueueBuilder.durable(DELETE_LETTER_BUSINESS_QUEUE_NAME_1).withArguments(args).build();
    }

    @Bean("deadLetterBusinessQueue2")
    public Queue deadLetterBusinessQueue2() {
        // return new Queue(DELETE_LETTER_BUSINESS_QUEUE_NAME_1);
        // 需要使用这种方式，创建队列。在创建的时候指定他的绑定的死信队列交换器和路由键
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_2);
        return QueueBuilder.durable(DELETE_LETTER_BUSINESS_QUEUE_NAME_2).withArguments(args).build();
    }

    @Bean("deadLetterBusinessQueue3")
    public Queue deadLetterBusinessQueue3() {
        // 需要使用这种方式，创建队列。在创建的时候指定他的绑定的死信队列交换器和路由键
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_3);
        // 设置队列过期时间，单位：毫秒
        args.put("x-message-ttl", 3000);
        // 设置队列最大长度
        args.put("x-max-length", 2);
        // 超过长度后，将其发送到死信队列
//        args.put("x-overflow", "reject-publish-dlx");
        return QueueBuilder.durable(DELETE_LETTER_BUSINESS_QUEUE_NAME_3).withArguments(args).build();
    }

    @Bean
    public Binding deadLetterBusinessBinding1(@Qualifier("deadLetterBusinessExchange") DirectExchange deadLetterBusinessExchange,
                                                @Qualifier("deadLetterBusinessQueue1") Queue deadLetterBusinessQueue1) {
        return BindingBuilder.bind(deadLetterBusinessQueue1).to(deadLetterBusinessExchange).with(DELETE_LETTER_BUSINESS_ROUTING_KEY_1);
    }

    @Bean
    public Binding deadLetterBusinessBinding2(@Qualifier("deadLetterBusinessExchange") DirectExchange deadLetterBusinessExchange,
                                              @Qualifier("deadLetterBusinessQueue2") Queue deadLetterBusinessQueue2) {
        return BindingBuilder.bind(deadLetterBusinessQueue2).to(deadLetterBusinessExchange).with(DELETE_LETTER_BUSINESS_ROUTING_KEY_2);
    }

    @Bean
    public Binding deadLetterBusinessBinding3(@Qualifier("deadLetterBusinessExchange") DirectExchange deadLetterBusinessExchange,
                                              @Qualifier("deadLetterBusinessQueue3") Queue deadLetterBusinessQueue3) {
        return BindingBuilder.bind(deadLetterBusinessQueue3).to(deadLetterBusinessExchange).with(DELETE_LETTER_BUSINESS_ROUTING_KEY_3);
    }


    @Bean("deadLetterDeadLetterExchange")
    public DirectExchange deadLetterDeadLetterExchange() {
        return new DirectExchange(DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME);
    }

    @Bean("deadLetterDeadLetterQueue1")
    public Queue deadLetterDeadLetterQueue1() {
        return new Queue(DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_1);
    }

    @Bean("deadLetterDeadLetterQueue2")
    public Queue deadLetterDeadLetterQueue2() {
        return new Queue(DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_2);
    }

    @Bean("deadLetterDeadLetterQueue3")
    public Queue deadLetterDeadLetterQueue3() {
        return new Queue(DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_3);
    }

    @Bean
    public Binding deadLetterDeadLetterBinding1(@Qualifier("deadLetterDeadLetterExchange") DirectExchange deadLetterDeadLetterExchange,
                                              @Qualifier("deadLetterDeadLetterQueue1") Queue deadLetterDeadLetterQueue1) {
        return BindingBuilder.bind(deadLetterDeadLetterQueue1).to(deadLetterDeadLetterExchange).with(DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_1);
    }

    @Bean
    public Binding deadLetterDeadLetterBinding2(@Qualifier("deadLetterDeadLetterExchange") DirectExchange deadLetterDeadLetterExchange,
                                              @Qualifier("deadLetterDeadLetterQueue2") Queue deadLetterDeadLetterQueue2) {
        return BindingBuilder.bind(deadLetterDeadLetterQueue2).to(deadLetterDeadLetterExchange).with(DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_2);
    }

    @Bean
    public Binding deadLetterDeadLetterBinding3(@Qualifier("deadLetterDeadLetterExchange") DirectExchange deadLetterDeadLetterExchange,
                                                @Qualifier("deadLetterDeadLetterQueue3") Queue deadLetterDeadLetterQueue3) {
        return BindingBuilder.bind(deadLetterDeadLetterQueue3).to(deadLetterDeadLetterExchange).with(DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_3);
    }

}
