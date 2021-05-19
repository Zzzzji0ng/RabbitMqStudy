package com.nd.listener;

import com.nd.config.DeadLetterConfig;
import com.nd.config.DelayedLetterConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DelayedLetterListener {


    @RabbitListener(queues = DelayedLetterConfig.DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_1)
    public void handleDeadLetterMessage1(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DelayedLetterConfig.DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_1 + " 的消息： " + new String(message.getBody()));
        System.out.println("成为死信的原因：" + message.getMessageProperties().getHeaders().get("x-first-death-reason"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayedLetterConfig.DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_2)
    public void handleDeadLetterMessage2(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DelayedLetterConfig.DELAYED_LETTER_DEAD_LETTER_QUEUE_NAME_2 + " 的消息： " + new String(message.getBody()));
        System.out.println("成为死信的原因：" + message.getMessageProperties().getHeaders().get("x-first-death-reason"));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = DelayedLetterConfig.DELAYED_LETTER_PLUGINS_QUEUE_NAME)
    public void handleDeadLetterMessage3(Message message, Channel channel) throws IOException {
        System.out.println("收到来自：" + DelayedLetterConfig.DELAYED_LETTER_PLUGINS_QUEUE_NAME + " 的延迟消息： " + new String(message.getBody()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
