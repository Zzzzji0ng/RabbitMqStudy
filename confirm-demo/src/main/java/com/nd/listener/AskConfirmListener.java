package com.nd.listener;

import com.nd.config.ConfirmDemoConsumerConfig;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * 手动确认模式需要实现这个接口，并重写抽象方法
 */
@Component
public class AskConfirmListener implements ChannelAwareMessageListener {
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        switch (message.getMessageProperties().getConsumerQueue()) {
            case ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_1:
                printMsgInfoAndAsk(message, channel);
                break;
            case ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_2:
                printMsgInfoAndRejectReQueue(message, channel);
                break;
            case ConfirmDemoConsumerConfig.ACK_QUEUE_NAME_3:
                printMsgInfoAndRejectNotReQueue(message, channel);
                break;
            default:
                printMsgInfoWithDefault(message, channel);
        }

    }

    private void printMsgInfoWithDefault(Message message, Channel channel) {
        System.out.println("printMsgInfoWithDefault msg : " + message.toString());
        try {
            // 手动确认消息，第一个参数是消息id
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            System.err.println("默认实现，接受没有队列信息的数据，实际上不可能走到，因为监听是队列基本的");
        }
    }

    private void printMsgInfoAndAsk(Message message, Channel channel) {
        System.out.println("printMsgInfoAndAsk msg : " + message.toString());
        try {
            // 手动确认消息，第一个参数是消息id
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            System.err.println("根据业务类型，进行日志记录或者是重新入队");
        }
    }

    private void printMsgInfoAndRejectReQueue(Message message, Channel channel) {
        System.out.println("printMsgInfoAndRejectReQueue msg : " + message.toString());
        try {
            // 手动拒绝消息，第一个参数是消息id, 重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        } catch (Exception e) {
            System.err.println("根据业务类型，进行日志记录或者是重新入队");
        }

    }

    private void printMsgInfoAndRejectNotReQueue(Message message, Channel channel) {
        System.out.println("printMsgInfoAndRejectNotReQueue msg : " + message.toString());
        try {
            // 手动拒绝消息，第一个参数是消息id, 不再重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            System.err.println("根据业务类型，进行日志记录或者是重新入队");
        }

    }
}
