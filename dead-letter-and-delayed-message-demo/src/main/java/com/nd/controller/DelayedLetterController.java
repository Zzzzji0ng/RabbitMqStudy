package com.nd.controller;

import com.nd.config.DelayedLetterConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/delayedLetter")
public class DelayedLetterController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsgToQueue1")
    public void sendMsgToQueue1() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(DelayedLetterConfig.DELAYED_LETTER_BUSINESS_EXCHANGE_NAME, DelayedLetterConfig.DELAYED_LETTER_BUSINESS_ROUTING_KEY_1, "sendMsgToQueue1 ：" + i);
        }
    }

    @GetMapping("/sendMsgToQueue2")
    public void sendMsgToQueue2() {
        for (int i = 10; i > 0; i--) {
            final int delayedTime = i * 100;
            // 发送过期时间从大到小的消息，后面过期时间短的消息，在消费时，直接就判断为过期了，直接扔到死信队列中去，不合理
            rabbitTemplate.convertAndSend(DelayedLetterConfig.DELAYED_LETTER_BUSINESS_EXCHANGE_NAME, DelayedLetterConfig.DELAYED_LETTER_BUSINESS_ROUTING_KEY_2, "sendMsgToQueue2 ：" + i,
                    msg -> {
                        msg.getMessageProperties().setDelay(delayedTime);
                        return msg;
                    }
            );
        }
    }

    @GetMapping("/sendMsgToDelayPluginExchange")
    public void sendMsgToQueue3() {
        for (int i = 10; i > 0; i--) {
            final int delayedTime = i * 1000;
            // 发送过期时间从大到小的消息，使用了延迟队列插件，预期是会优先消费过期时间短的消息
            rabbitTemplate.convertAndSend(DelayedLetterConfig.DELAYED_LETTER_PLUGINS_EXCHANGE_NAME, DelayedLetterConfig.DELAYED_LETTER_PLUGINS_ROUTING_KEY, "sendMsgToDelayPluginExchange ：" + i,
                    msg -> {
                        // 在消息头中设置过期时间
                        msg.getMessageProperties().setHeader("x-delay", delayedTime);
                        return msg;
                    }
            );
        }
    }
}
