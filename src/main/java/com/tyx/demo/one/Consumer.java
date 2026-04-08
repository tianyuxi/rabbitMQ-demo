package com.tyx.demo.one;

import com.rabbitmq.client.*;

/**
 * @author 田宇希
 * @version 1.0
 * @description: TODO
 * @date 2026/3/12 19:42
 */
public class Consumer{
    //队列的名称
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTag,message)->{
            System.out.println(new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTag ->{
            System.out.println("消费消息被中断");
        };
        /*
        * 消费者消费消息
        * 1.消费哪个队列
        * 2.消费成功之后是否要自动应答，true表示自动应答，f表示手动应答
        * 3.消费者未成功消费的回调
        * 4.消费者取消消费的回调
        * */

        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);


    }
}
