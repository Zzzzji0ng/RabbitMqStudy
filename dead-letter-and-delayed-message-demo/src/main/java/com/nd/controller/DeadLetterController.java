package com.nd.controller;

import com.nd.config.DeadLetterConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deadLetter")
public class DeadLetterController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMegToQueue1")
    public void sendMegToQueue1() {
        rabbitTemplate.convertAndSend(DeadLetterConfig.DELETE_LETTER_BUSINESS_EXCHANGE_NAME, DeadLetterConfig.DELETE_LETTER_BUSINESS_ROUTING_KEY_1, "sendMegToQueue1");
    }

    @GetMapping("/sendMegToQueue2")
    public void sendMegToQueue2() {
        rabbitTemplate.convertAndSend(DeadLetterConfig.DELETE_LETTER_BUSINESS_EXCHANGE_NAME, DeadLetterConfig.DELETE_LETTER_BUSINESS_ROUTING_KEY_2, "sendMegToQueue2");
    }

    @GetMapping("/sendMsgToQueue3")
    public void sendMsgToQueue3() {
        for (int i = 0; i < 10; i++) {
            rabbitTemplate.convertAndSend(DeadLetterConfig.DELETE_LETTER_BUSINESS_EXCHANGE_NAME, DeadLetterConfig.DELETE_LETTER_BUSINESS_ROUTING_KEY_3, "sendMegToQueue3 ：" + i);
        }
    }
}
