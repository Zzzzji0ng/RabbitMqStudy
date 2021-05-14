package com.nd.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class TopicProviderConfig {

    public static final String SIMPLE_TOPIC_EXCHANGE_NAME = "simple-topic-exchange";

    public static final String SIMPLE_TOPIC_QUEUE_NAME1 = "simple-topic-queue-1";

    public static final String SIMPLE_TOPIC_QUEUE_NAME2 = "simple-topic-queue-2";

    public static final String SIMPLE_TOPIC_QUEUE_NAME3 = "simple-topic-queue-3";

    @Bean
    public Queue queue1() {
        return new Queue(SIMPLE_TOPIC_QUEUE_NAME1);
    }

    @Bean
    public Queue queue2() {
        return new Queue(SIMPLE_TOPIC_QUEUE_NAME2);
    }

    @Bean
    public Queue queue3() {
        return new Queue(SIMPLE_TOPIC_QUEUE_NAME3);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(SIMPLE_TOPIC_EXCHANGE_NAME);
    }

    // 使用不包含通配符的路由键绑定交换器和队列
    @Bean
    public Binding bindingByName() {
        return BindingBuilder.bind(queue1()).to(topicExchange()).with(SIMPLE_TOPIC_QUEUE_NAME1);
    }

    // 使用包含*的路由键绑定交换器和队列，*表示一个单词
    @Bean
    public Binding bindingByTopicSingle() {
        return BindingBuilder.bind(queue2()).to(topicExchange()).with("topic.*.test");
    }

    // 使用包含#的路由键绑定交换器和队列，#表示零个或多个单词
    @Bean
    public Binding bindingByTopicMulti() {
        return BindingBuilder.bind(queue3()).to(topicExchange()).with("topic.#");
    }

}
