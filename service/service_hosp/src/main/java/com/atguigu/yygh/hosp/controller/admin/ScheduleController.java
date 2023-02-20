package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/15 14:39
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R detail(@PathVariable String hoscode,
                    @PathVariable String depcode,
                    @PathVariable String workdate){
        List<Schedule> list = scheduleService.detail(hoscode,depcode,workdate);
        return R.ok().data("list",list);
    }

    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R page(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize,
                  @PathVariable("hoscode") String hoscode,@PathVariable("depcode") String depcode){

        Map<String,Object> map = scheduleService.page(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }


}
