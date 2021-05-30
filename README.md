# RabbitMqStudy

### 1. 基本概念及应用场景

#### 基本概念：

| 标志   | 中文名        | 英文名   | 描述                                             |
| ------ | ------------- | -------- | ------------------------------------------------ |
| P      | 生产者        | Producer | 消息的发送者，可以将消息发送到交换机             |
| C      | 消费者        | Consumer | 消息的接收者，从队列中获取消息并进行消费         |
| X      | 交换机        | Exchange | 接收生产者发送的消息，并根据路由键发送给指定队列 |
| Q      | 队列          | Queue    | 存储从交换机发来的消息                           |
| type   | 交换机类型    | type     | 不同类型的交换机转发消息方式不同                 |
| fanout | 发布/订阅模式 | fanout   | 广播消息给所有绑定交换机的队列                   |
| direct | 路由模式      | direct   | 根据路由键发送消息                               |
| topic  | 通配符模式    | topic    | 根据路由键的匹配规则发送消息                     |

#### 消息模式：

##### 简单模式：

![img](http://www.macrozheng.com/images/rabbitmq_start_12.png)

简单模式是最简单的消息模式，它包含一个生产者、一个消费者和一个队列。生产者向队列里发送消息，消费者从队列中获取消息并消费。

##### 工作模式：

![img](http://www.macrozheng.com/images/rabbitmq_start_15.png)

工作模式是指向多个互相竞争的消费者发送消息的模式，它包含一个生产者、两个消费者和一个队列。两个消费者同时绑定到一个队列上去，当消费者获取消息处理耗时任务时，空闲的消费者从队列中获取并消费消息。



##### 发布订阅模式（Fanout）

### ![img](http://www.macrozheng.com/images/rabbitmq_start_19.png)

广播的形式，会将消息发送到交换器绑定的所有队列上



##### 路由模式：Direct

![img](http://www.macrozheng.com/images/rabbitmq_start_19.png)

队列和交换器是使用路由键绑定的，路由键需要全匹配。消息发送的时候根据携带的路由键转发到不同的队列上。



##### 通配符模式 Topic：

![img](http://www.macrozheng.com/images/rabbitmq_start_27.png)

和路由模式很像，但是路由键支持匹配的方式，比较灵活。

- *：只能匹配一个单词；
- `#`：可以匹配零个或多个单词



### 2. 消息的可靠性

#### 消息发送的状态回调

配置项中需要开启两个回调：

```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: zjy
    password: 123456
    # 设置了一个虚拟主机
    virtual-host: /zjy
    publisher-confirms: true #消息推送到交换机回调，需要在RabbitTemplate配置中实现回调处理
    publisher-returns: true #消息推送到队列回调，需要在RabbitTemplate配置中实现回调处理
```

rabbitmqTemplate 需要实现部分回调

```java
// 这个是消息发送到 blocker 之后，就会回调这个方法
rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("ConfirmCallback 相关数据 correlationData : " + correlationData);
        System.out.println("ConfirmCallback 确认情况 : " + ack);
        System.out.println("ConfirmCallback 原因 : " + cause);
    }
});

// 发送到具体的队列后，会回调这个方法
rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("ReturnCallback 消息体 : " + message.toString());
        System.out.println("ReturnCallback 响应码 : " + replyCode);
        System.out.println("ReturnCallback 响应内容 : " + replyText);
        System.out.println("ReturnCallback 交换器 : " + exchange);
        System.out.println("ReturnCallbacjk 路由键 : " + routingKey);
    }
});
```

各种异常情况的回调处理：

1. 发送的交换器不存在
   1. 只会调用 ConfirmCallback 方法，回调中可以看到是路由不存在
2. 发送的交换器存在，队列不存在
   1. 两个方法都会被调用，ConfirmCallback 中可以看到是路由失败

#### 如何保证消息能够正确的发送到Mq中

1. 事务模式

   1. 通过事务的方式，保证消息能够正常的发送的MQ中

   2. 配置事务管理器, 同时在发送方法中，使用@Transactional注解来使事务生效

      1. ```java
         /**
         * 配置启用rabbitmq事务，如果要使用事务，需要申明一个事务管理器即可
         */
         @Bean
         public RabbitTransactionManager rabbitTransactionManager(CachingConnectionFactory connectionFactory) {
             return new RabbitTransactionManager(connectionFactory);
         }
         ```

      2. ```java
         // 使用 Transactional 注解来标记事务管理
         @Transactional
         public void sendMsg(String exchange, String routingKey, String msg) {
             rabbitTemplate.convertAndSend(exchange, routingKey, msg);
             // 如果内容中包好了 exception , 那么抛出异常，触发事务
             System.out.println("调用发送消息" + msg);
             if (msg != null && msg.contains("exception")) throw new RuntimeException("surprise!");
             System.out.println("正常发送消息" + msg);
         }
         ```

   3. 当队列或交换器不存在的时候，不会抛出异常，事务能够正常执行

      

2. 消息确认模式

   1. 通过内置的消息确认方式，保证消息能够正常的发送到MQ中

   2. 开启消息确认的配置，不能与事务共同使用。

      1. ```yaml
         spring:
           rabbitmq:
             publisher-confirms: true # 开启生产者确认模式
         ```

      2. ```java
         // 需要实现两个接口，分别处理消息到达Exchange的情况和消息路由情况
         public class ConfirmProviderProducer implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback{
         
         /**
              *
              * 是否到达交换器的回调
              *
              * Confirmation callback.
              * @param correlationData correlation data for the callback.
              * @param ack true for ack, false for nack
              * @param cause An optional cause, for nack, when available, otherwise null.
              */
             @Override
             public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                 System.out.println("发送回调确认：" + mapMessage.get(correlationData.getId()));
                 if (ack) {
                     System.out.println("发送结果成功：" + mapMessage.get(correlationData.getId()));
                 } else {
                     System.out.println("发送结果失败：" + mapMessage.get(correlationData.getId()) + ", 原因：" + cause);
                 }
             }
         
             /**
              * 交换器没找路由，退回的消息信息
              *
              * Returned message callback.
              * @param message the returned message.
              * @param replyCode the reply code.
              * @param replyText the reply text.
              * @param exchange the exchange.
              * @param routingKey the routing key.
              */
             @Override
             public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                 System.out.println("消息被服务器退回。msg:" + message + ", replyCode: " + replyCode + ". replyText: " + replyText
                                    + ", exchange: " + exchange + ", routingKey :" + routingKey);
             }
         ```

      3. ```java
         // 需要在生产者处开启消息确认模式，并指定回调的实现
         @Autowired
         private RabbitTemplate rabbitTemplate;
         
         public void init() {
             // 在实例创建的时候，确认消息回调, 用于标记是否到达交换器
             rabbitTemplate.setConfirmCallback(this);
             // 交换机无法将消息进行路由时，会将该消息返回给生产者，而如果该参数设置为false，如果发现消息无法进行路由，则直接丢弃
             rabbitTemplate.setMandatory(true);
             // 开启消息路由失败回调
             rabbitTemplate.setReturnCallback(this);
             mapMessage = new HashMap<>();
         }
         ```

      4. 使用备份路由，当消息无法被路由到指定的队列后，可以被转发到备份路由上，而不是被直接丢弃

         1. ```java
            // 绑定备份交换器, 使用这个参数 alternate-exchange
            @Bean("confirmProviderExchange1")
            public DirectExchange confirmProviderExchange1() {
            // 使用这种方式创建
            return (DirectExchange)ExchangeBuilder.directExchange(CONFIRM_PROVIDER_EXCHANGE_NAME_1).durable(true)
            .withArgument("alternate-exchange", CONFIRM_PROVIDER_EXCHANGE_NAME_BACK).build();
            }
            ```

   3. 两种方式对比：
      1. 事务方式发布消息，性能太差，往往不采用事务的方式发布消息，建议采用异步发送确认的方式

#### 消息的持久化功能

​	消息的持久化，分为几个维度

1. ​	Exchange 的持久化，在创建的时候，指定 Exchange 的 durable 为 true。开启了后，当 Blocker 重启后，会重新创建交换器，避免后续的消息无法接受
2.    Queue 的持久化，当 Exchange 开启持久化后，如果队列没有持久化，重启后队列就会丢失，里面的消息自然也会丢失。通过 durable 设置为 True 后，来开启队列的持久化。
3.   Message 的持久化，即使 Exchange 和 Queue 都开启持久化后，重启后消息会丢失。因此在发送消息时，需要指定消息的持久化。通过投递时设置投递模式 deliveryMode = 2 , 来指定消息的持久化
4. 开启HA, 同时设置镜像队列 mirrored-queue。当消息成功发送到 Blocker 后，数据还在内存中，暂未落盘，此时断电等时间发生，消息丢失，存在镜像队列，降低该事件的发生概率。
5. 消息落盘的时机
   1. 缓存区满
   2. 固定刷盘时间到
6. 消费者需要通过手动确认模式，来保证消息的消费反馈。autoAck设置为false

#### 消费者的成功消费

​	需要开启手动确认模式，保证消息被成功消费

#### 

### 3. 死信队列和延迟队列的使用

#### 	死信队列

1. 消息怎么样会变成死信

   1. 消息被否定确认，使用 `channel.basicNack` 或 `channel.basicReject` ，并且此时`requeue` 属性被设置为`false`。
   2. 消息在队列的存活时间超过设置的TTL时间。
   3. 消息队列的消息数量已经超过最大队列长度。

2. 死信会怎么处理它

   1. 如果配置了死信队列信息，那么该消息将会被丢进死信队列中，如果没有配置，则该消息将会被丢弃。

3. 如何配置死信队列

   1. ```java
      @Bean("deadLetterBusinessQueue1")
      public Queue deadLetterBusinessQueue1() {
          // return new Queue(DELETE_LETTER_BUSINESS_QUEUE_NAME_1);
          // 需要使用这种方式，创建队列。在创建的时候指定他的绑定的死信队列交换器和路由键
          Map<String, Object> args = new HashMap<>();
          args.put("x-dead-letter-exchange", DELETE_LETTER_DEAD_LETTER_EXCHANGE_NAME);
          args.put("x-dead-letter-routing-key", DELETE_LETTER_DEAD_LETTER_ROUTING_KEY_1);
          return QueueBuilder.durable(DELETE_LETTER_BUSINESS_QUEUE_NAME_1).withArguments(args).build();
      }
      ```

   2. ```java
      // 消息被判定为死信后，并被投递到对应的死信交换器后，会在消息属性中加上一些特殊的信息
      @RabbitListener(queues = DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_3)
      public void handleDeadLetterMessage3(Message message, Channel channel) throws IOException {
          System.out.println("收到来自：" + DeadLetterConfig.DELETE_LETTER_DEAD_LETTER_QUEUE_NAME_3 + " 的消息： " + new String(message.getBody()));
          System.out.println("成为死信的原因：" + message.getMessageProperties().getHeaders().get("x-first-death-reason"));
          channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
      }
      
      /**
      header 中会加入一些值
               *      x-first-death-exchange  被抛入的死信交换机的名称
               *      x-first-death-queue     成为死信前所在队列名称
               *      x-first-death-reason    成为死信的原因，rejected：消息在重新进入队列时被队列拒绝，由于default-requeue-rejected 参数被设置为false。expired ：消息过期。maxlen ： 队列内消息数量超过队列最大容量
      *
      */
      ```

   3. 可以使用死信队列加上特定的过期时间，来实现一个延迟处理的消息队列，但是有一定的缺点

      1. 使用 x-message-ttl 设置 Queue 的过期时间
      2. 使用 properties.Expiration 设置消息的过期时间
      3. 两者都有的情况下，以消息短的为准
      4. 使用队列设置消息过期有一个问题，当消息积压后，后面的消息都会过期
      5. 不同业务的过期时间不同，需要设置不同的过期时间的队列
      6. 如果使用消息体的过期时间，当前一个消息的过期时间大于后一个消息的过期时间，那么后一个消息一定会过期。
      7. 消息是否过期，是消息到达队列头部的时候，进行检查的。

#### 	延迟队列

1. 使用官方提供的延迟队列插件，来实现延迟队列的功能 rabbitmq_delayed_message_exchange

2. ```java
   // 在交换器上，设置为死信交换器
   @Bean("delayedLetterPluginsExchange")
   public Exchange delayedLetterPluginsExchange() {
       // 使用这种方式，标记使用延迟插件创建交换器
       Map<String, Object> args = new HashMap<>();
       args.put("x-delayed-type", "direct");
       // 需要指定 type 为 x-delayed-message， 并将参数带入
       return new CustomExchange(DELAYED_LETTER_PLUGINS_EXCHANGE_NAME, "x-delayed-message", true, false, args);
   }
   ```

3. 原理：

   1. 通过 x-delayed-message 声明的交换机，它的消息在发布之后不会立即进入队列，先将消息保存至 Mnesia（一个分布式数据库管理系统，适合于电信和其它需要持续运行和具备软实时特性的 Erlang 应用。目前资料介绍的不是很多）
   2. 检测消息延迟时间，如达到可投递时间时并将其通过`x-delayed-type`类型标记的交换机类型投递至目标队列