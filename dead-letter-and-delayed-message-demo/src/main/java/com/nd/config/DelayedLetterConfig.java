package com.nd.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedLetterConfig {

    // 业务队列交换器名
    public static final String DELAYED_LETTER_BUSINESS_EXCHANGE_NAME = "delayed_letter_business_exchange_name";

    // 业务队列名1
    public static final String DELAYED_LETTER_BUSINESS_QUEUE_NAME_1 = "delayed_letter_business_queue_name_1";

    // 业务队列名2
    public static final String DELAYED_LETTER_BUSINESS_QUEUE_NAME_2 = "delayed_letter_business_queue_name_2";

    // 业务队列名3
    public static final String DELAYED_LETTER_BUSINESS_QUEUE_NAME_3 = "delayed_letter_business_queue_name_3";

    // 业务路由键1
    public static final String DELAYED_LETTER_BUSINESS_ROUTING_KEY_1 = "delayed_letter_business_routing_key_1";

    // 业务路由键2
    public static final String DELAYED_LETTER_BUSINESS_ROUTING_KEY_2 = "delayed_letter_business_routing_key_2";

    // 业务路由键3
    public static final String DELAYED_LETTER_BUSINESS_ROUTING_KEY_3 = "delayed_letter_business_routing_key_3";

    // 死信队列交换器名
    public static final String DELAYED_LETTER_DEAD_LETTER_EXCHANGE_NAME = "delayed_letter_dead_letter_exchange_name";

    // 死信队列名1
    public static final String DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_1 = "delayed_letter_dead_letter_queue_name_1";

    // 死信队列名2
    public static final String DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_2 = "delayed_letter_dead_letter_queue_name_2";

    // 死信路由键1
    public static final String DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_1 = "delayed_letter_dead_letter_routing_key_1";

    // 死信路由键2
    public static final String DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_2 = "delayed_letter_dead_letter_routing_key_2";


    // 延迟队列插件实现
    // 延迟交换器名
    public static final String DELAYED_LETTER_PLUGINS_EXCHANGE_NAME = "delayed_letter_plugins_exchange_name";

    // 延迟队列名
    public static final String DELAYED_LETTER_PLUGINS_QUEUE_NAME = "delayed_letter_plugins_queue_name";

    // 延迟路由键
    public static final String DELAYED_LETTER_PLUGINS_ROUTING_KEY = "delayed_letter_plugins_routing_key";




    @Bean("delayedLetterBusinessExchange")
    public DirectExchange delayedLetterBusinessExchange() {
        return new DirectExchange(DELAYED_LETTER_BUSINESS_EXCHANGE_NAME);
    }

    // 这种方式，每个业务的过期时间需求不同，需要设置多个队列，实现不优雅
    @Bean("delayedLetterBusinessQueue1")
    public Queue delayedLetterBusinessQueue1() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DELAYED_LETTER_DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_1);
        args.put("x-message-ttl", 2000);
        return QueueBuilder.durable(DELAYED_LETTER_BUSINESS_QUEUE_NAME_1).withArguments(args).build();
    }

    // 队列本身不设置过期时间，让消息自己过期
    @Bean("delayedLetterBusinessQueue2")
    public Queue delayedLetterBusinessQueue2() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DELAYED_LETTER_DEAD_LETTER_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_2);
        return QueueBuilder.durable(DELAYED_LETTER_BUSINESS_QUEUE_NAME_2).withArguments(args).build();
    }

    @Bean
    public Binding delayedLetterBusinessBinding1(@Qualifier("delayedLetterBusinessExchange") DirectExchange delayedLetterBusinessExchange,
                                                @Qualifier("delayedLetterBusinessQueue1") Queue delayedLetterBusinessQueue1) {
        return BindingBuilder.bind(delayedLetterBusinessQueue1).to(delayedLetterBusinessExchange).with(DELAYED_LETTER_BUSINESS_ROUTING_KEY_1);
    }

    @Bean
    public Binding delayedLetterBusinessBinding2(@Qualifier("delayedLetterBusinessExchange") DirectExchange delayedLetterBusinessExchange,
                                              @Qualifier("delayedLetterBusinessQueue2") Queue delayedLetterBusinessQueue2) {
        return BindingBuilder.bind(delayedLetterBusinessQueue2).to(delayedLetterBusinessExchange).with(DELAYED_LETTER_BUSINESS_ROUTING_KEY_2);
    }


    @Bean("delayedLetterDelayedLetterExchange")
    public DirectExchange delayedLetterDelayedLetterExchange() {
        return new DirectExchange(DELAYED_LETTER_DEAD_LETTER_EXCHANGE_NAME);
    }

    @Bean("delayedLetterDelayedLetterQueue1")
    public Queue delayedLetterDelayedLetterQueue1() {
        return new Queue(DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_1);
    }

    @Bean("delayedLetterDelayedLetterQueue2")
    public Queue delayedLetterDelayedLetterQueue2() {
        return new Queue(DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_2);
    }

    @Bean
    public Binding delayedLetterDelayedLetterBinding1(@Qualifier("delayedLetterDelayedLetterExchange") DirectExchange delayedLetterDelayedLetterExchange,
                                              @Qualifier("delayedLetterDelayedLetterQueue1") Queue delayedLetterDelayedLetterQueue1) {
        return BindingBuilder.bind(delayedLetterDelayedLetterQueue1).to(delayedLetterDelayedLetterExchange).with(DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_1);
    }

    @Bean
    public Binding delayedLetterDelayedLetterBinding2(@Qualifier("delayedLetterDelayedLetterExchange") DirectExchange delayedLetterDelayedLetterExchange,
                                                      @Qualifier("delayedLetterDelayedLetterQueue2") Queue delayedLetterDelayedLetterQueue2) {
        return BindingBuilder.bind(delayedLetterDelayedLetterQueue2).to(delayedLetterDelayedLetterExchange).with(DELAYED_LETTER_DEAD_LETTER_ROUTING_KEY_2);
    }

    // 使用延迟队列插件的方式实现延迟队列
    @Bean("delayedLetterPluginsExchange")
    public Exchange delayedLetterPluginsExchange() {
        // 使用这种方式，标记使用延迟插件创建交换器
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        // 需要指定 type 为 x-delayed-message， 并将参数带入
        return new CustomExchange(DELAYED_LETTER_PLUGINS_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    // 延迟队列
    @Bean("delayedLetterPluginsQueue")
    public Queue delayedLetterPluginsQueue() {
        return new Queue(DELAYED_LETTER_PLUGINS_QUEUE_NAME, true);
    }

    @Bean
    public Binding delayedLetterPluginsBinding(@Qualifier("delayedLetterPluginsExchange") Exchange delayedLetterPluginsExchange,
                                                 @Qualifier("delayedLetterPluginsQueue") Queue delayedLetterPluginsQueue) {
        return BindingBuilder.bind(delayedLetterPluginsQueue).to(delayedLetterPluginsExchange).with(DELAYED_LETTER_PLUGINS_ROUTING_KEY).noargs();
    }

    /**
     * 实现原理
     * 上面使用 DLX + TTL 的模式，消息首先会路由到一个正常的队列，根据设置的 TTL 进入死信队列，与之不同的是通过 x-delayed-message 声明的交换机，它的消息在发布之后不会立即进入队列，先将消息保存至 Mnesia（一个分布式数据库管理系统，适合于电信和其它需要持续运行和具备软实时特性的 Erlang 应用。目前资料介绍的不是很多）
     *
     * 这个插件将会尝试确认消息是否过期，首先要确保消息的延迟范围是 Delay > 0, Delay =< ?ERL_MAX_T（在 Erlang 中可以被设置的范围为 (2^32)-1 毫秒），如果消息过期通过 x-delayed-type 类型标记的交换机投递至目标队列，整个消息的投递过程也就完成了。
     */

}
