package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 21:41
 * @Description: everything is ok
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Resource
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        String toJSONString = JSONObject.toJSONString(map);
        Schedule schedule = JSONObject.parseObject(toJSONString, Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule platformSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode ,hosScheduleId );

        if(platformSchedule == null){
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);

            scheduleRepository.save(schedule);
        }else {
            schedule.setUpdateTime(platformSchedule.getCreateTime());
            schedule.setId(platformSchedule.getId());
            schedule.setIsDeleted(platformSchedule.getIsDeleted());

            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getSchedulePage(Map<String, Object> map) {
        Schedule schedule = new Schedule();
        String hoscode = (String)map.get("hoscode");
        schedule.setHoscode(hoscode);

        Example<Schedule> scheduleExample = Example.of(schedule);

        Integer pageNum = Integer.parseInt((String)map.get("page"));
        Integer limit = Integer.parseInt((String)map.get("limit"));
        //记住要pageNum减一!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        PageRequest pageRequest = PageRequest.of(pageNum - 1, limit, Sort.by("createTime").ascending());

        Page<Schedule> all = scheduleRepository.findAll(scheduleExample, pageRequest);

        return all;
    }

    @Override
    public void remove(Map<String, Object> map) {
        String hoscode = (String)map.get("hoscode");
        String hosScheduleId = (String)map.get("hosScheduleId");

        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);

        if(schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}
