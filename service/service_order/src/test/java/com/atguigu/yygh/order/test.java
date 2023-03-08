package com.atguigu.yygh.order;

import com.atguigu.yygh.hosp.client.ScheduleFeighClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.sun.corba.se.spi.ior.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: fs
 * @date: 2023/3/5 20:10
 * @Description: everything is ok
 */
@SpringBootTest
public class test {


    @Autowired
    private ScheduleFeighClient scheduleFeighClient;
    @Test
    public void test(){
        ScheduleOrderVo scheduleById = scheduleFeighClient.getScheduleById("63fc6a37c58b875a15a01984");
        System.out.println(scheduleById.toString());
    }
}
