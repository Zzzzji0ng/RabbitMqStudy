package com.nd.controller;

import com.nd.config.FanoutRabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController(value = "/fanoutRabbit")
public class FanoutRabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public void sendMessageToMq(String content) {
        if (StringUtils.isEmpty(content)) {
            content = "fanout message ";
        }

        for (int i = 0; i < 10; i++) {
            Map<String, Object> mapInfo = new HashMap<>();
            mapInfo.put("msgId", UUID.randomUUID());
            mapInfo.put("content", content + " " + i);
            mapInfo.put("createTime", LocalTime.now());
            // 这里不需要指定路由键
            rabbitTemplate.convertAndSend(FanoutRabbitConfig.SIMPLE_FANOUT_EXCHANGE_NAME, null, mapInfo);
        }
    }

}
