package com.nd.listener;

import com.nd.config.FanoutRabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

// 设置监听的队列名称
@Component
@RabbitListener(queues = FanoutRabbitConfig.SIMPLE_FANOUT_QUEUE_NAME3)
public class FanoutListener3 {

    @RabbitHandler
    public void processMessage(Map<String, Object> message) {
        System.out.println("FanoutListener3 消费了信息： msg: " + message.toString());
    }

}
