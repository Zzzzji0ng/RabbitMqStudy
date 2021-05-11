package com.nd.controller;

import com.nd.config.DirectRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController(value = "/directRabbit")
public class DirectRabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping
    public void sendMessageToMq(String content) {
        if (StringUtils.isEmpty(content)) {
            content = "test prefix ";
        }

        for (int i = 0; i < 10; i++) {
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put("msgId", UUID.randomUUID());
            mapInfo.put("content", content + " " + i);
            mapInfo.put("createTime", LocalTime.now());
            rabbitTemplate.convertAndSend(DirectRabbitConfig.SIMPLE_DIRECT_EXCHANGE_NAME,
                    DirectRabbitConfig.SIMPLE_DIRECT_ROUTING_KEY, mapInfo);
        }
    }

}
