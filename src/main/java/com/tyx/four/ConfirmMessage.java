package com.tyx.four;

/*
* 发布确认模式
* 使用的时间 比较哪种确认方式是最好的
* 1、单个确认
* 2、批量确认
* 3、异步批量确认
* */

import com.rabbitmq.client.Channel;
import com.tyx.utils.RabbitMqUtils;

import java.util.UUID;

public class ConfirmMessage {

    //批量发消息的个数
    public static final int message_count = 1000;
    public static void main(String[] args) throws Exception{
        //1、单个确认
        ConfirmMessage.publishMessageIndividually();
        //2、批量确认
        //3、异步批量确认
    }

    //单个确认
    public static void publishMessageIndividually() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();
        //批量的发信息
        for (int i = 0; i < message_count; i++) {
            String message = i + "";
            channel.basicPublish("",queueName,null,message.getBytes());
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布"+message_count+"个单独确认消息，耗时"+(end-begin)+"ms");
    }

    public static void publishMessageBatch() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        for (int i = 0; i < message_count; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //发布确认
            channel.waitForConfirms();
        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+message_count+"个单独确认消息，耗时"+(end-begin)+"ms");
    }
}
