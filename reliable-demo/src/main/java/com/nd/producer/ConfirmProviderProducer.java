package com.nd.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ConfirmProviderProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Map<String, String> mapMessage;

    @PostConstruct
    public void init() {
        // 在实例创建的时候，确认消息回调, 用于标记是否到达交换器
        rabbitTemplate.setConfirmCallback(this);
        // 交换机无法将消息进行路由时，会将该消息返回给生产者，而如果该参数设置为false，如果发现消息无法进行路由，则直接丢弃
        rabbitTemplate.setMandatory(true);
        // 开启消息路由失败回调
        rabbitTemplate.setReturnCallback(this);
        mapMessage = new HashMap<>();
    }

    // 使用消息确认模式后不可使用注解
    //    @Transactional
    public void sendMsg(String exchange, String routingKey, String msg) {
        //为消息设置了消息ID，以便在回调时通过该ID来判断是对哪个消息的回调，因为在回调函数中，我们是无法直接获取到消息内容的
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        mapMessage.put(correlationData.getId(), msg);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
        // 如果内容中包好了 exception , 那么抛出异常，触发事务
        System.out.println("调用发送消息" + msg);
        if (msg != null && msg.contains("exception")) throw new RuntimeException("surprise!");
        System.out.println("正常发送消息" + msg);
    }

    /**
     *
     * 是否到达交换器的回调
     *
     * Confirmation callback.
     * @param correlationData correlation data for the callback.
     * @param ack true for ack, false for nack
     * @param cause An optional cause, for nack, when available, otherwise null.
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("发送回调确认：" + mapMessage.get(correlationData.getId()));
        if (ack) {
            System.out.println("发送结果成功：" + mapMessage.get(correlationData.getId()));
        } else {
            System.out.println("发送结果失败：" + mapMessage.get(correlationData.getId()) + ", 原因：" + cause);
        }
    }

    /**
     * 交换器没找路由，退回的消息信息
     *
     * Returned message callback.
     * @param message the returned message.
     * @param replyCode the reply code.
     * @param replyText the reply text.
     * @param exchange the exchange.
     * @param routingKey the routing key.
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息被服务器退回。msg:" + message + ", replyCode: " + replyCode + ". replyText: " + replyText
                + ", exchange: " + exchange + ", routingKey :" + routingKey);
    }
}
