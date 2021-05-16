package com.nd.controller;

import com.nd.config.ConfirmDemoConsumerConfig;
import com.nd.config.ConfirmDemoProviderConfig;
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

    @GetMapping("/sendMsgToExchangeWithNotExistQueue")
    public void sendMsgToExchangeWithNotExistQueue() {
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("key", "value");
        // 发送到不存在的交换器去
        rabbitTemplate.convertAndSend(ConfirmDemoProviderConfig.DIRECT_EXCHANGE_WITHOUT_QUEUE, "not-exists-routing-key", mapInfo);

        /**
         两个回调方法都会被调用到

         回调内容
         ReturnCallback 消息体 : (Body:'{key=value}' MessageProperties [headers={}, contentType=application/x-java-serialized-object, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, deliveryTag=0])
         ReturnCallback 响应码 : 312
         ReturnCallback 响应内容 : NO_ROUTE
         ReturnCallback 交换器 : direct_exchange_without_queue
         ReturnCallback 路由键 : not-exists-routing-key

         ConfirmCallback 相关数据 correlationData : null
         ConfirmCallback 确认情况 : true
         ConfirmCallback 原因 : null
         */
    }

    @GetMapping("/sendMsgToExchangeWithQueue")
    public void sendMsgToExchangeWithQueue() {
        Map<String, Object> mapInfo = new HashMap<>();
        mapInfo.put("key", "value");
        // 发送到不存在的交换器去
        rabbitTemplate.convertAndSend(ConfirmDemoProviderConfig.DIRECT_EXCHANGE_WITH_QUEUE, ConfirmDemoProviderConfig.DIRECT_ROUTING_KEY, mapInfo);

        /**
         只会回调这个方法，消息回调的方法不会被调用
         *
         ConfirmCallback 相关数据 correlationData : null
         ConfirmCallback 确认情况 : true
         ConfirmCallback 原因 : null
         */
    }

    @GetMapping("/sendMsgAndAsk")
    public void sendMsgAndAsk() {
        String msg = " sendMsgAndAsk ";
        // 发送到队列1中，会手动确认消费
        rabbitTemplate.convertAndSend("", ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_1, msg);
    }

    @GetMapping("/sendMsgAndRejectRequeue")
    public void sendMsgAndRejectRequeue() {
        String msg = " sendMsgAndRejectRequeue ";
        // 发送到队列2中，会手动拒绝消费，然后重新入队,预期会出现循环消费
        rabbitTemplate.convertAndSend("", ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_2, msg);
    }

    @GetMapping("/sendMsgAndRejectNotRequeue")
    public void sendMsgAndRejectNotRequeue() {
        String msg = " sendMsgAndRejectNotRequeue ";
        // 发送到队列3中，会手动拒绝消费，然后不会重新入队
        rabbitTemplate.convertAndSend("", ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_3, msg);
    }
}
