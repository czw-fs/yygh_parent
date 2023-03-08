package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: fs
 * @date: 2023/3/5 16:50
 * @Description: everything is ok
 */
@FeignClient(value = "service-hosp")
public interface ScheduleFeighClient {

    @GetMapping("/user/hosp/schedule/{scheduleId}")
    public ScheduleOrderVo getScheduleById(@PathVariable("scheduleId") String scheduleId);
}
