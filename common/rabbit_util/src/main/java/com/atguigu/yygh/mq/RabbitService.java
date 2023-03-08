package com.atguigu.yygh.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: fs
 * @date: 2023/3/6 9:19
 * @Description: everything is ok
 */
@Component
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }

}
