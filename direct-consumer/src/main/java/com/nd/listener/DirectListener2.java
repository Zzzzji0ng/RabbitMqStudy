package com.nd.listener;

import com.nd.config.DirectRabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

// 设置监听的队列名称
@Component
@RabbitListener(queues = DirectRabbitConfig.SIMPLE_DIRECT_QUEUE_NAME)
public class DirectListener2 {

    /**
     * 存在多个消费者的话，会轮询消费，且不会重复消费
     * @param message
     */
    @RabbitHandler
    public void processMessage(Map<String, Object> message) {
        System.out.println("DirectListener2消费了信息： msg: " + message.toString());
    }

}
