package com.atguigu.yygh.mq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author: fs
 * @date: 2023/3/6 9:20
 * @Description: everything is ok
 */
@SpringBootConfiguration
public class RabbitConfig {

    //将发送到rabbitmq中的pojo对象自动转换为json格式存储
    //从rabbitmq中消费消息时,自动将json字符串转换为pojo对象
    @Bean
    public MessageConverter getMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
