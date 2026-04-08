package com.tyx.demo.three;

import com.rabbitmq.client.Channel;
import com.tyx.utils.RabbitMqUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Task2 {

    //队列名称
    public static final String ACK_QUEUE_NAME = "ack_name";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        channel.queueDeclare(ACK_QUEUE_NAME,false,false,false,null);
        //从控制台输入
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("",ACK_QUEUE_NAME,null,message.getBytes(StandardCharsets.UTF_8));
            System.out.println("生产者发出消息"+message);
        }

    }

}
