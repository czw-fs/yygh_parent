package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.mq.MqConst;
import com.atguigu.yygh.mq.RabbitService;
import com.atguigu.yygh.vo.msm.MsmVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: fs
 * @date: 2023/3/6 15:35
 * @Description: everything is ok
 */
@Component
public class OrderMqListener {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_ORDER,durable = "true"),//创建队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER),//创建交换机
                    key = MqConst.ROUTING_ORDER
            )
    })
    //确认挂号
    //取消预约
    public void consumer(OrderMqVo orderMqVo , Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        MsmVo msmVo = null;
        if(availableNumber != null){
            Boolean flag = scheduleService.updateAvailableNumber(scheduleId,availableNumber);
            msmVo = orderMqVo.getMsmVo();
        }else {
            scheduleService.cannelSchedule(scheduleId);
        }

        if(msmVo != null){
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }

    }

}
