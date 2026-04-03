package com.tyx.four;

/*
* 发布确认模式
* 使用的时间 比较哪种确认方式是最好的
* 1、单个确认
* 2、批量确认
* 3、异步批量确认
* */

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.tyx.utils.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConfirmMessage {

    //批量发消息的个数
    public static final int message_count = 1000;
    public static void main(String[] args) throws Exception{
        //1、单个确认
        //ConfirmMessage.publishMessageIndividually();
        //2、批量确认
        //ConfirmMessage.publishMessageBatch();
        //3、异步批量确认，性能高，效率好
        ConfirmMessage.publishMessageAsync();
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
        //批量确认的长度
        int batchSize = 100;


        for (int i = 0; i < message_count; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());

            if(i%batchSize == 0){
                channel.waitForConfirms();
            }

        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+message_count+"个批量确认消息，耗时"+(end-begin)+"ms");
    }

    public static void publishMessageAsync() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,false,false,false,null);
        channel.confirmSelect();
        /*
        * 线程安全有序的哈希表 适用于高并发的情况下
        * 1.轻松的将序号与消息进行关联
        * 2.轻松批量删除条且 只要给到序号
        * 3.支持高并发（多线程）
        * */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();


        ConfirmCallback ackCallback = (deliveryTag,multiple)->{
            //成功
            if(multiple){
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            }else {
                outstandingConfirms.remove(deliveryTag);
            }

            System.out.println("确认的消息"+deliveryTag);
        };
        ConfirmCallback nackCallback = (deliveryTag,multiple)->{
            //失败
            String message = outstandingConfirms.get(deliveryTag);

            System.out.println("未确认的消息是"+message+"未确认的消息tag"+deliveryTag);
        };
        //准备消息的监听器 监听哪些消息成功了 哪些消息失败了
        channel.addConfirmListener(ackCallback,nackCallback);//异步通知

        //开始时间
        long begin = System.currentTimeMillis();
        for (int i = 0; i < message_count; i++) {
            String message = "消息"+i;
            channel.basicPublish("",queueName,null,message.getBytes());
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }

        long end = System.currentTimeMillis();
        System.out.println("发布"+message_count+"个异步发布确认消息，耗时"+(end-begin)+"ms");
    }
}
