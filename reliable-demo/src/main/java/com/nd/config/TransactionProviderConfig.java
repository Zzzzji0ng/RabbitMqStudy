package com.nd.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionProviderConfig {

    public static final String TRANSACTION_EXCHANGE_NAME = "transaction_exchange_name";

    public static final String TRANSACTION_QUEUE_NAME = "transaction_queue_name";

    public static final String TRANSACTION_ROUTING_KEY_WITH_QUEUE = "transaction_routing_key_with_queue";

    // 这个路由键没有队列跟他绑定
    public static final String TRANSACTION_ROUTING_KEY_WITHOUT_QUEUE = "transaction_routing_key_without_queue";


    @Bean("transactionExchange")
    public DirectExchange transactionExchange() {
        return new DirectExchange(TRANSACTION_EXCHANGE_NAME);
    }

    @Bean("transactionQueue")
    public Queue transactionQueue() {
        return new Queue(TRANSACTION_QUEUE_NAME);
    }

    @Bean("transactionBinding")
    public Binding transactionBinding(@Qualifier("transactionExchange") DirectExchange transactionExchange,
                                      @Qualifier("transactionQueue") Queue transactionQueue) {
        return BindingBuilder.bind(transactionQueue()).to(transactionExchange()).with(TRANSACTION_ROUTING_KEY_WITH_QUEUE);
    }

    /**
     * 配置启用rabbitmq事务，如果要使用事务，需要申明一个事务管理器即可
     */
    @Bean
    public RabbitTransactionManager rabbitTransactionManager(CachingConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }
}
