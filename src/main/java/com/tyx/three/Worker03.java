package com.tyx.three;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.tyx.utils.RabbitMqUtils;
import com.tyx.utils.SleepUtils;

import java.nio.charset.StandardCharsets;


//消息在手动应答时是不丢失、放回队列重新消费
public class Worker03 {

    public static final String ACK_QUEUE_NAME = "ack_name";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C1等待接受消息处理时间较短");

        //采用手动应答
        DeliverCallback deliverCallback = ( var1, var2)->{
            SleepUtils.sleep(1);
            System.out.println("接收到的消息"+new String(var2.getBody(), StandardCharsets.UTF_8));
            /**
             * 1.消息标记 tag
             * 2.是否批量应答未应答消息
             */
            channel.basicAck(var2.getEnvelope().getDeliveryTag(),false);
        };


        boolean autoAck = false;
        channel.basicConsume(ACK_QUEUE_NAME,autoAck,deliverCallback,(consumerTag)-> {
            System.out.println(consumerTag + "消费者取消消费接口回调逻辑");
        });
    }
}
