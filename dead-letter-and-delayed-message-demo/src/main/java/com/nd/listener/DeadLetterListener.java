package com.nd.listener;

import com.nd.config.DeadLetterConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeadLetterListener {

    @RabbitListener(queues = DeadLetterConfig.DELETE_LETTER_BUSINESS_QUEUE_NAME_1)
    public void handleBusinessMessage1(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DeadLetterConfig.DELETE_LETTER_BUSINESS_QUEUE_NAME_1 + " 的消息： " + new String(message.getBody()));
        // 收到确认，并且不重新入队
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DeadLetterConfig.DELETE_LETTER_BUSINESS_QUEUE_NAME_2)
    public void handleBusinessMessage2(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DeadLetterConfig.DELETE_LETTER_BUSINESS_QUEUE_NAME_2 + " 的消息： " + new String(message.getBody()));
        System.out.println(DeadLetterConfig.DELETE_LETTER_BUSINESS_QUEUE_NAME_2 + " ：原消息properties：" + message.getMessageProperties());

        /**
         * 消息内容
         *
         * [
         * headers={},
         * contentType=text/plain,
         * contentEncoding=UTF-8,
         * contentLength=0,
         * receivedDeliveryMode=PERSISTENT,
         * priority=0,
         * redelivered=false,
         * receivedExchange=delete_letter_business_exchange_name,
         * receivedRoutingKey=delete_letter_business_routing_key_2,
         * deliveryTag=1,
         * consumerTag=amq.ctag-VFaQj7znVzQ6XefQz9_6YA,
         * consumerQueue=delete_letter_business_queue_name_2
         * ]
         *
         */

        // 收到拒绝，并且不重新入队
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
    }

    @RabbitListener(queues = DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_1)
    public void handleDeadLetterMessage1(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_1 + " 的消息： " + new String(message.getBody()));

        // 收到死信队列1中的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }

    @RabbitListener(queues = DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_2)
    public void handleDeadLetterMessage2(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_2 + " 的消息： " + new String(message.getBody()));
        System.out.println(DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_2 + " ：死信消息properties：" + message.getMessageProperties());

        /**
         * 消息内容
         *
         * [
         *  headers={
         *      header 中会加入一些值
         *      x-first-death-exchange  被抛入的死信交换机的名称
         *      x-first-death-queue     成为死信前所在队列名称
         *      x-first-death-reason    成为死信的原因，rejected：消息在重新进入队列时被队列拒绝，由于default-requeue-rejected 参数被设置为false。expired ：消息过期。maxlen ： 队列内消息数量超过队列最大容量
         *
         *
         *      x-first-death-exchange=delete_letter_business_exchange_name,
         *      x-death=[
         *          {reason=rejected,
         *          count=1,
         *          exchange=delete_letter_business_exchange_name,
         *          time=Mon May 17 21:07:39 CST 2021,
         *          routing-keys=[delete_letter_business_routing_key_2],
         *          queue=delete_letter_business_queue_name_2}],
         *          x-first-death-reason=rejected,
         *          x-first-death-queue=delete_letter_business_queue_name_2},
         *   contentType=text/plain,
         *   contentEncoding=UTF-8,
         *   contentLength=0,
         *   receivedDeliveryMode=PERSISTENT,
         *   priority=0,
         *   redelivered=false,
         *   接受队列和交换器会发生变化
         *   receivedExchange=delete_letter_dead_letter_exchange_name,
         *   receivedRoutingKey=delete_letter_dead_letter_routing_key_2,
         *   deliveryTag=1,
         *   consumerTag=amq.ctag-IWBsz_UDEmvIL1RWMjYk-A,
         *   consumerQueue=delete_letter_dead_letter_queue_name_2]
         *
         */

        // 收到死信队列2中的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
    }
}
