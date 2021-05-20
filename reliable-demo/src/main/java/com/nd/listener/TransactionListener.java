package com.nd.listener;

import com.nd.config.TransactionProviderConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {

    @RabbitListener(queues = TransactionProviderConfig.TRANSACTION_QUEUE_NAME)
    public void receiveMsg(Message message) {
        System.out.println("收到业务消息 : " + new String(message.getBody()));
    }

}
