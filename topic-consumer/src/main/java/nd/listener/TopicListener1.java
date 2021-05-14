package nd.listener;

import nd.config.TopicConsumerConfig;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

// 设置监听的队列名称
@Component
@RabbitListener(queues = TopicConsumerConfig.SIMPLE_TOPIC_QUEUE_NAME1)
public class TopicListener1 {

    @RabbitHandler
    public void processMessage(Map<String, Object> message) {
        System.out.println("TopicListener1消费了信息： msg: " + message.toString());
    }

}
