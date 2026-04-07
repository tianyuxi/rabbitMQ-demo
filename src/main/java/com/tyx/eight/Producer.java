package com.tyx.eight;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.tyx.utils.RabbitMqUtils;

public class Producer {
    public static final String normal_exchange = "normal_exchange";
    public static void main(String[] args) throws Exception{

        Channel channel = RabbitMqUtils.getChannel();
        //死信消息 设置ttl消息 time to live
        AMQP.BasicProperties properties = new AMQP.BasicProperties()
                .builder().expiration("10000").build();
        for (int i = 1; i < 11; i++) {
            String message = "info"+i;
            channel.basicPublish(normal_exchange,"zhangsan",null,message.getBytes());

        }

    }
}
