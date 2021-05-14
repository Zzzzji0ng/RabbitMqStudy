package com.nd.controller;

import com.nd.config.TopicProviderConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/topicRabbit")
public class TopicProviderController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping(value = "/sendMessageByName")
    public void sendMessageByName() {
        String content = "sendMessageByName   ";
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put("msgId", UUID.randomUUID());
            mapInfo.put("content", content + i);
            // 这个消息会发送这个交换器，使用的路由键是不包含通配符的字符串，将会转发到其中一个绑定的队列
            rabbitTemplate.convertAndSend(TopicProviderConfig.SIMPLE_TOPIC_EXCHANGE_NAME, TopicProviderConfig.SIMPLE_TOPIC_QUEUE_NAME1, mapInfo);
        }
    }

    @GetMapping(value = "/sendMessageBySingle")
    public void sendMessageBySingle() {
        String content = "sendMessageBySingle   ";
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put("msgId", UUID.randomUUID());
            mapInfo.put("content", content + i);
            // 这个消息会发送这个交换器，使用的路由键是包含通配符*的字符串，将会转发到其中两个绑定的队列
            rabbitTemplate.convertAndSend(TopicProviderConfig.SIMPLE_TOPIC_EXCHANGE_NAME, "topic.hello.test", mapInfo);
        }
    }

    @GetMapping(value = "/sendMessageByMulti")
    public void sendMessageByMulti() {
        String content = "sendMessageByMulti   ";
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put("msgId", UUID.randomUUID());
            mapInfo.put("content", content + i);
            // 这个消息会发送这个交换器，使用的路由键是包含通配符#的字符串，将会转发到其中一个绑定的队列
            rabbitTemplate.convertAndSend(TopicProviderConfig.SIMPLE_TOPIC_EXCHANGE_NAME, "topic.hello.world", mapInfo);
        }
    }
}
