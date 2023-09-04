# Spring Boot引入RabbitMQ

## 添加Maven依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

## 添加RabbitMQ相关配置

在`application.yml`文件中添加以下配置

```yaml
spring:
  rabbitmq:
    host: xxx
    port: xxx
    virtual-host: /
    username: xxx
    password: xxx
    publisher-confirm-type: correlated # 开启发送确认
    listener:
      simple:
        acknowledge-mode: manual # 消费端手动确认
```

`spring.rabbitmq.publisher-confirm-type=correlated`用于开启发送确认，可以在消息发送成功或失败时执行回调方法：

```java
// 设置发送确认回调
rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
    if (ack) {
        // TODO 消息发送成功
    } else {
        // TODO 消息发送失败
    }
});
```

`spring.rabbitmq.listener.simple.acknowledge-mode=manual`用于开启消费端手动确认，可以在收到消息时手动回复ack，或者拒签：

```java
class MyListener {
    @RabbitHandler
    public void process(@Payload String msg, Message message, Channel channel) throws IOException {
        // TODO 消息处理逻辑
        
        // 确认消息接收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        
        // 拒签消息并重新入队
        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
    }
}
```

## 声明队列和交换机

声明direct交换机：

```java
@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-direct-queue"),
        exchange = @Exchange("my-direct-exchange")
    )
)
class MyDirectQueueListener {
    // ...
}
```

声明fanout交换机：

```java
@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-fanout-queue1"),
        exchange = @Exchange(value = "my-fanout-exchange", type = ExchangeTypes.FANOUT)
    )
)
class MyFanoutQueueListener1 {
    // ...
}

@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-fanout-queue2"),
        exchange = @Exchange(value = "my-fanout-exchange", type = ExchangeTypes.FANOUT)
    )
)
class MyFanoutQueueListener2 {
    // ...
}
```
