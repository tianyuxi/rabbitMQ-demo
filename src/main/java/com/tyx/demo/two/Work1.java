package com.tyx.demo.two;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tyx.utils.RabbitMqUtils;

/**
 * @author 田宇希
 * @version 1.0
 * @description: TODO
 * @date 2026/3/12 21:26
 */
public class Work1 {

    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        DeliverCallback deliverCallback = (consumer,message)->{
            System.out.println("收到的消息"+new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
