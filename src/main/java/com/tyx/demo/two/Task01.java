package com.tyx.demo.two;

import com.rabbitmq.client.Channel;
import com.tyx.utils.RabbitMqUtils;

import java.util.Scanner;

/**
 * @author 田宇希
 * @version 1.0
 * @description: TODO
 * @date 2026/3/15 9:46
 * 生产者，可以发大量的消息
 */
public class Task01 {

    //队列名称
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();

        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNext()){
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息完成: "+message);
        }
    }
}
