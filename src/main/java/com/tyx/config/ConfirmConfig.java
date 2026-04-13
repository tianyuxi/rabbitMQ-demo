package com.tyx.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfirmConfig {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm.queue";

    //备份的数据
    public static final String confirm_routing_key = "key1";

    public static final String backup_exchange_name = "backup_exchange";
    public static final String backup_queue_name = "backup_queue";
    public static final String warning_queue_name = "warning_queue";

    //声明业务 Exchange
//    @Bean("confirmExchange")
//    public DirectExchange confirmExchange() {
//        //return new DirectExchange(CONFIRM_EXCHANGE_NAME);
//    }

    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {

        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
                .durable(true)
//设置该交换机的备份交换机
                .withArgument("alternate-exchange", backup_exchange_name).build();
    }


    // 声明确认队列
    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE_NAME).build();
    }

    // 声明确认队列绑定关系
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                @Qualifier("confirmExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("key1");
    }

    @Bean
    public FanoutExchange backupExchange() {
        return new FanoutExchange(backup_exchange_name);
    }

    @Bean
    public Queue backupQueue() {
        return QueueBuilder.durable(backup_queue_name).build();
    }

    @Bean
    public Queue warningQueue() {
        return QueueBuilder.durable(warning_queue_name).build();
    }

    @Bean
    public Binding backupBinding(@Qualifier("backupQueue") Queue backupQueue,
                                 @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    @Bean
    public Binding warningBinding(@Qualifier("warningQueue") Queue warningQueue,
                                  @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }


}
