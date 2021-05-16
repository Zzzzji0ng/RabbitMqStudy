package com.nd.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmDemoProviderConfig {

    public static final String DIRECT_EXCHANGE_WITHOUT_QUEUE = "direct_exchange_without_queue";

    public static final String DIRECT_EXCHANGE_WITH_QUEUE = "direct_exchange_with_queue";

    public static final String DIRECT_QUEUE = "direct_queue";

    public static final String DIRECT_ROUTING_KEY = "direct_routing_key";

    @Bean
    public RabbitTemplate createRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMandatory(true); // 设置强制开启回调

        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback 相关数据 correlationData : " + correlationData);
                System.out.println("ConfirmCallback 确认情况 : " + ack);
                System.out.println("ConfirmCallback 原因 : " + cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback 消息体 : " + message.toString());
                System.out.println("ReturnCallback 响应码 : " + replyCode);
                System.out.println("ReturnCallback 响应内容 : " + replyText);
                System.out.println("ReturnCallback 交换器 : " + exchange);
                System.out.println("ReturnCallback 路由键 : " + routingKey);
            }
        });

        return rabbitTemplate;
    }

    @Bean
    public DirectExchange directExchangeWithoutQueue() {
        return new DirectExchange(DIRECT_EXCHANGE_WITHOUT_QUEUE);
    }

    @Bean
    public DirectExchange directExchangeWithQueue() {
        return new DirectExchange(DIRECT_EXCHANGE_WITH_QUEUE);
    }

    @Bean
    public Queue directQueue() {
        return new Queue(DIRECT_QUEUE);
    }

    @Bean
    public Binding directBinding() {
        return BindingBuilder.bind(directQueue()).to( directExchangeWithQueue()).with(DIRECT_ROUTING_KEY);
    }
}
