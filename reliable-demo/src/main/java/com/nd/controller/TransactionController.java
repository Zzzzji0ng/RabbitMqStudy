package com.nd.controller;

import com.nd.config.TransactionProviderConfig;
import com.nd.producer.TransactionProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactionController")
public class TransactionController {

    @Autowired
    private TransactionProducer transactionProducer;

    @RequestMapping("sendMsgWithTransaction")
    public void sendMsgWithTransaction(String msg) {
        transactionProducer.sendMsg(TransactionProviderConfig.TRANSACTION_EXCHANGE_NAME, TransactionProviderConfig.TRANSACTION_ROUTING_KEY_WITH_QUEUE, msg);
        /**
         *
         * 没有触发异常的日志输出：
         *
         * 调用发送消息hello,123
         * 正常发送消息hello,123
         * 收到业务消息 : hello,123
         *
         *
         * 触发了异常的日志输出，没有将消息发送到mqBlocker中，但是效率较低
         * 调用发送消息exception
         *
         */
    }

    @RequestMapping("sendMsgWithTransactionInNotQueue")
    public void sendMsgWithTransactionInNotQueue(String msg) {
        transactionProducer.sendMsg(TransactionProviderConfig.TRANSACTION_EXCHANGE_NAME, TransactionProviderConfig.TRANSACTION_ROUTING_KEY_WITHOUT_QUEUE, msg);
        /**
         *
         * 即使是发送到不存在的队列，还是发送成功，不会抛出异常
         *
         * 调用发送消息exceptio1
         * 正常发送消息exceptio1
         *
         */
    }

}
