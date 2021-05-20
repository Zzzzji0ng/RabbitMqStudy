package com.nd.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmProviderConfig {

    public static final String CONFIRM_PROVIDER_EXCHANGE_NAME = "confirm_provider_exchange_name";

    public static final String CONFIRM_PROVIDER_EXCHANGE_NAME_NOT_EXISTS = "confirm_provider_exchange_name_not_exists";

    public static final String CONFIRM_PROVIDER_QUEUE_NAME = "confirm_provider_queue_name";

    public static final String CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE = "confirm_provider_routing_key_with_queue";

    // 这个路由键没有队列跟他绑定
    public static final String CONFIRM_ROUTING_KEY_WITHOUT_QUEUE = "confirm_routing_key_without_queue";


    // 备份交换器逻辑
    public static final String CONFIRM_PROVIDER_EXCHANGE_NAME_1 = "confirm_provider_exchange_name_1";

    public static final String CONFIRM_PROVIDER_QUEUE_NAME_1 = "confirm_provider_queue_name_1";

    public static final String CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE_1 = "confirm_provider_routing_key_with_queue_1";

    public static final String CONFIRM_PROVIDER_EXCHANGE_NAME_BACK = "confirm_provider_exchange_name_back";

    public static final String CONFIRM_PROVIDER_QUEUE_NAME_BACK = "confirm_provider_queue_name_back";




    @Bean("confirmProviderExchange")
    public DirectExchange confirmProviderExchange() {
        return new DirectExchange(CONFIRM_PROVIDER_EXCHANGE_NAME);
    }

    @Bean("confirmProviderQueue")
    public Queue confirmProviderQueue() {
        return new Queue(CONFIRM_PROVIDER_QUEUE_NAME);
    }

    @Bean("confirmProviderBinding")
    public Binding confirmProviderBinding(@Qualifier("confirmProviderExchange") DirectExchange confirmProviderExchange,
                                      @Qualifier("confirmProviderQueue") Queue confirmProviderQueue) {
        return BindingBuilder.bind(confirmProviderQueue).to(confirmProviderExchange).with(CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE);
    }


    // 绑定备份交换器
    @Bean("confirmProviderExchange1")
    public DirectExchange confirmProviderExchange1() {
        // 使用这种方式创建
        return (DirectExchange)ExchangeBuilder.directExchange(CONFIRM_PROVIDER_EXCHANGE_NAME_1).durable(true)
                .withArgument("alternate-exchange", CONFIRM_PROVIDER_EXCHANGE_NAME_BACK).build();
    }

    @Bean("confirmProviderQueue1")
    public Queue confirmProviderQueue1() {
        return new Queue(CONFIRM_PROVIDER_QUEUE_NAME_1);
    }

    @Bean("confirmProviderBinding1")
    public Binding confirmProviderBinding1(@Qualifier("confirmProviderExchange1") DirectExchange confirmProviderExchange1,
                                          @Qualifier("confirmProviderQueue1") Queue confirmProviderQueue1) {
        return BindingBuilder.bind(confirmProviderQueue1).to(confirmProviderExchange1).with(CONFIRM_PROVIDER_ROUTING_KEY_WITH_QUEUE_1);
    }


    @Bean("confirmProviderExchangeBack")
    public FanoutExchange confirmProviderExchangeBack() {
        return new FanoutExchange(CONFIRM_PROVIDER_EXCHANGE_NAME_BACK);
    }

    @Bean("confirmProviderQueueBack")
    public Queue confirmProviderQueueBack() {
        return new Queue(CONFIRM_PROVIDER_QUEUE_NAME_BACK);
    }

    @Bean("confirmProviderBindingBack")
    public Binding confirmProviderBindingBack(@Qualifier("confirmProviderExchangeBack") FanoutExchange confirmProviderExchangeBack,
                                          @Qualifier("confirmProviderQueueBack") Queue confirmProviderQueueBack) {
        return BindingBuilder.bind(confirmProviderQueueBack).to(confirmProviderExchangeBack);
    }
}
