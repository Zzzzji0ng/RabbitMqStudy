package com.nd.controller;

import com.nd.config.ConfirmProviderConfig;
import com.nd.config.TransactionProviderConfig;
import com.nd.producer.ConfirmProviderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirmProviderController")
public class ConfirmProviderController {

    @Autowired
    private ConfirmProviderProducer confirmProviderProducer;

    @RequestMapping("sendMsgWithConfirm")
    public void sendMsgWithTransaction(String msg) {
        confirmProviderProducer.sendMsg(ConfirmProviderConfig.CONFIRM_PROVIDER_EXCHANGE_NAME, ConfirmProviderConfig.CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE, msg);

        /**
         *
         * 交换器和队列都在的情况下，能够正常发送，并确认收到
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         * 收到业务消息 : exceptio1
         * 发送回调确认：exceptio1
         * 发送结果成功：exceptio1
         *
         */
    }

    @RequestMapping("sendMsgWithConfirmNotExistsExchange")
    public void sendMsgWithConfirmNotExistsExchange(String msg) {
        confirmProviderProducer.sendMsg(ConfirmProviderConfig.CONFIRM_PROVIDER_EXCHANGE_NAME_NOT_EXISTS, ConfirmProviderConfig.CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE, msg);

        /**
         *
         * 交换器不在，但是队列在的情况下，回调结果中，表示发送失败，原因是交换器不存在
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         * 2021-05-20 19:40:05.808 ERROR 83000 --- [ 127.0.0.1:5672] o.s.a.r.c.CachingConnectionFactory       :
         * Channel shutdown: channel error; protocol method: #method<channel.close>
         * (reply-code=404, reply-text=NOT_FOUND - no exchange 'confirm_provider_exchange_name_not_exists' in vhost '/zjy', class-id=60, method-id=40)
         * 发送回调确认：exceptio1
         * 发送结果失败：exceptio1, 原因：channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'confirm_provider_exchange_name_not_exists' in vhost '/zjy', class-id=60, method-id=40)
         *
         */

    }

    @RequestMapping("sendMsgWithConfirmNotExistsQueue")
    public void sendMsgWithConfirmNotExistsQueue(String msg) {
        confirmProviderProducer.sendMsg(ConfirmProviderConfig.CONFIRM_PROVIDER_EXCHANGE_NAME, ConfirmProviderConfig.CONFIRM_ROUTING_KEY_WITHOUT_QUEUE, msg);

        /**
         *
         * 交换器在，但是队列不存在的情况下，回调结果中是发布成功，消息体内部消化了，需要再实现一个回调接口，确认是否到达队列
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         * 发送回调确认：exceptio1
         * 发送结果成功：exceptio1
         *
         *
         *
         * 开启了 Mandatory 和 returnedMessage  回调之后，会收到被丢弃的消息，可以使用定时任务进行重试或其他异常处理逻辑
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         * 消息被服务器退回。msg:(Body:'exceptio1'
         * MessageProperties [
         *      headers={spring_returned_message_correlation=b32ede90-fee8-4fd1-9b13-fcaf800a5fee},
         *      contentType=text/plain,
         *      contentEncoding=UTF-8,
         *      contentLength=0,
         *      receivedDeliveryMode=PERSISTENT,
         *      priority=0, deliveryTag=0]),
         * replyCode: 312. replyText: NO_ROUTE,
         * exchange: confirm_provider_exchange_name,
         * routingKey :confirm_routing_key_without_queue
         * 发送回调确认：exceptio1
         * 发送结果成功：exceptio1
         *
         */
    }


    @RequestMapping("sendMsgWithConfirmBackNotExistsQueue")
    public void sendMsgWithConfirmBackNotExistsQueue(String msg) {
        confirmProviderProducer.sendMsg(ConfirmProviderConfig.CONFIRM_PROVIDER_EXCHANGE_NAME_1, ConfirmProviderConfig.CONFIRM_ROUTING_KEY_WITHOUT_QUEUE, msg);

        /**
         *
         * 该交换器绑定了备份交换器，即使配置了 setMandatory 和 setReturnCallback，也不会回调到这个回调方法，没有成功路由的消息会被路由到备份交换器，可以再备份交换器队列
         * 的消费者处进行数据的消费
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         * 收到备份消息 : exceptio1
         * 发送回调确认：exceptio1
         * 发送结果成功：exceptio1
         *
         */
    }

}
