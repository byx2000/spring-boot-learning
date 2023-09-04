package byx.test;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
class MyController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        // 设置发送确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                System.out.println("message send successful");
            } else {
                System.out.println("message send failed: " + cause);
            }
        });
    }

    @GetMapping("/send-to-direct")
    public void sendToDirect(String msg) {
        rabbitTemplate.convertAndSend("my-direct-exchange", "", msg);
    }

    @GetMapping("/send-to-fanout")
    public void sendToFanout(String msg) {
        rabbitTemplate.convertAndSend("my-fanout-exchange", "", msg);
    }
}

@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-direct-queue"),
        exchange = @Exchange("my-direct-exchange")
    )
)
class MyDirectQueueListener {
    @RabbitHandler
    public void process(@Payload String msg, Message message, Channel channel) throws IOException {
        System.out.println("receive from direct queue: " + msg);
        // 手动确认消息接收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-fanout-queue1"),
        exchange = @Exchange(value = "my-fanout-exchange", type = ExchangeTypes.FANOUT)
    )
)
class MyFanoutQueueListener1 {
    @RabbitHandler
    public void process(@Payload String msg, Message message, Channel channel) throws IOException {
        System.out.println("listener1 receive from fanout queue: " + msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}

@Component
@RabbitListener(
    bindings = @QueueBinding(
        value = @Queue("my-fanout-queue2"),
        exchange = @Exchange(value = "my-fanout-exchange", type = ExchangeTypes.FANOUT)
    )
)
class MyFanoutQueueListener2 {
    @RabbitHandler
    public void process(@Payload String msg, Message message, Channel channel) throws IOException {
        System.out.println("listener2 receive from fanout queue: " + msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}