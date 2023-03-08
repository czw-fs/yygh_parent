package com.atguigu.yygh.task.job;

import com.atguigu.yygh.mq.MqConst;
import com.atguigu.yygh.mq.RabbitService;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: fs
 * @date: 2023/3/8 14:54
 * @Description: everything is ok
 */
@Component
public class PatientRemindJob {

    //Quartz:cron表达式:秒分时dayofMonth Month dayofWeek Year[最高到2099年]
    //* :表示任意xxx
    //?:表示无所谓
    //-:连续的时间段
    //n:表示每隔多长时间
    //,:可以使用,隔开没有规律的时间

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "*/30 * * * * ?")
    public void printTime(){
        System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"hello world");
    }

    //在springboot定时任务使用:1.在主启动类上加eEnableScheduling 2.在定时任务Job的方法上加eScheduled并指定石英表达式
    //cron表达式写法:七域表达式

}
