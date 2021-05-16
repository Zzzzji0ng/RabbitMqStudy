package com.nd.config;

import com.nd.listener.AskConfirmListener;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmDemoConsumerConfig {

    @Autowired
    private AskConfirmListener askConfirmListener;

    public static final String ACK_QUEUE_NAME_1 = "ack_queue_name_1";
    public static final String ACK_QUEUE_NAME_2 = "ack_queue_name_2";
    public static final String ACK_QUEUE_NAME_3 = "ack_queue_name_3";

    @Bean
    public SimpleMessageListenerContainer getSimpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        // 设置并发消费者的初始化数量，这里设置为一个
        simpleMessageListenerContainer.setConcurrentConsumers(1);
        // 设置消费者最大并发数量.,这里设置1个
        simpleMessageListenerContainer.setMaxConcurrentConsumers(1);
        //  设置为手动确认模式，AUTO, MANUAL, NONE , NONE 代表无确认模式，会有丢失消息的风险
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置监听那些队列
        simpleMessageListenerContainer.addQueues(ackQueue1());
        simpleMessageListenerContainer.addQueues(ackQueue2());
        simpleMessageListenerContainer.addQueues(ackQueue3());
        // 设置处理类
        simpleMessageListenerContainer.setMessageListener(askConfirmListener);
        return simpleMessageListenerContainer;
    }

    @Bean
    public Queue ackQueue1() {
        return new Queue(ACK_QUEUE_NAME_1);
    }

    @Bean
    public Queue ackQueue2() {
        return new Queue(ACK_QUEUE_NAME_2);
    }

    @Bean
    public Queue ackQueue3() {
        return new Queue(ACK_QUEUE_NAME_3);
    }
}
