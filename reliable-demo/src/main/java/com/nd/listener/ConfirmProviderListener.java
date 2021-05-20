package com.nd.listener;

import com.nd.config.ConfirmProviderConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfirmProviderListener {

    @RabbitListener(queues = ConfirmProviderConfig.CONFIRM_PROVIDER_QUEUE_NAME)
    public void receiveMsg(Message message) {
        System.out.println("收到业务消息 : " + new String(message.getBody()));
    }

    @RabbitListener(queues = ConfirmProviderConfig.CONFIRM_PROVIDER_QUEUE_NAME_BACK)
    public void receiveMsgBack(Message message) {
        System.out.println("收到备份消息 : " + new String(message.getBody()));
    }

}
