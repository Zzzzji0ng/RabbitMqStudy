package com.nd.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/confirmDemo")
public class ConfirmDemoController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsgToNotExistExchange")
    public void sendMsgToNotExistExchange() {
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("key", "value");
        // 发送到不存在的交换器去
        rabbitTemplate.convertAndSend("not-exists-exchange", "not-exists-routing-key", mapInfo);

        /*
        这种情况只会回调 ConfirmCallback 方法，里面可以明确看到是交换器不存在
        ConfirmCallback 返回信息
        ConfirmCallback 相关数据 correlationData : null
        ConfirmCallback 确认情况 : false
        ConfirmCallback 原因 : channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'not-exists-exchange' in vhost '/zjy', class-id=60, method-id=40)
        */
    }

}
