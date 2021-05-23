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



##### 通配符模式：

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

#### 消息发送到服务端的各种情况及处理



#### 消息的持久化功能

#### 消费者的成功消费