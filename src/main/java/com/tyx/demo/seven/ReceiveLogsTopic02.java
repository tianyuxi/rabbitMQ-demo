package com.tyx.demo.seven;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.tyx.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;

/**
 * 声明主题交换机 及相关队列
 */
public class ReceiveLogsTopic02 {
    //交换机的名称
    public static final String exchange_name = "topic_logs";
    //接受消息

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(exchange_name, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueName = "Q2";
        channel.queueDeclare(queueName,false,false,false,null);

        channel.queueBind(queueName,exchange_name,"*.*.rabbit");
        channel.queueBind(queueName,exchange_name,"lazy.*");
        System.out.println("等待接受消息");
        DeliverCallback deliverCallback =(consumerTag, delivery)->{
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" 接收队列 :"+queueName+" 绑 定 键:"+delivery.getEnvelope().getRoutingKey()+",消息:"+message);

        };
        channel.basicConsume(queueName,true,deliverCallback, consumerTag -> {});

    }
}
