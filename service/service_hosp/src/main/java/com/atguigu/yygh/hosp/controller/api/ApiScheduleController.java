package com.atguigu.yygh.hosp.controller.api;


import com.atguigu.yygh.hosp.bean.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 21:37
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {


    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/schedule/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

        //验证sign~~

        scheduleService.remove(map);
        return Result.ok();
    }

    @PostMapping("/schedule/list")
    public Result getSchedulePage(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        Page<Schedule> schedulePage = scheduleService.getSchedulePage(map);
        return Result.ok(schedulePage);
    }


    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

        scheduleService.saveSchedule(map);
        return Result.ok();
    }
}
