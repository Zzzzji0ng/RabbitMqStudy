package com.nd.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
public class TransactionProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @PostConstruct
    public void init() {
        // 在实例创建的时候，设置开启事务
        rabbitTemplate.setChannelTransacted(true);
    }

    // 使用 Transactional 注解来标记事务管理
    @Transactional
    public void sendMsg(String exchange, String routingKey, String msg) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
        // 如果内容中包好了 exception , 那么抛出异常，触发事务
        System.out.println("调用发送消息" + msg);
        if (msg != null && msg.contains("exception")) throw new RuntimeException("surprise!");
        System.out.println("正常发送消息" + msg);
    }
}
