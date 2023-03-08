package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import javafx.concurrent.ScheduledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/25 11:51
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getScheduleById(@PathVariable("scheduleId") String scheduleId){
        return scheduleService.getScheduleById(scheduleId);
    }


    //根据排班id获取排班信息
    @GetMapping("/info/{scheduleId}")
    public R getSchelduelInfo(@PathVariable String scheduleId){
        Schedule schedule = scheduleService.getSchelduelInfo(scheduleId);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/{hoscode}/{depcode}/{pageNum}/{pageSize}")
    public R getSchedulePage(@PathVariable String hoscode,
                             @PathVariable String depcode,
                             @PathVariable Integer pageNum,
                             @PathVariable Integer pageSize){
        Map<String ,Object> map = scheduleService.getSchedulePageByCondition(hoscode,depcode,pageNum,pageSize);
        return R.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workdate){
        List<Schedule> details = scheduleService.detail(hoscode, depcode, workdate);
        return R.ok().data("details",details);
    }

}
