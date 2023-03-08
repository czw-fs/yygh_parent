package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 21:40
 * @Description: everything is ok
 */
public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> getSchedulePage(Map<String, Object> map);

    void remove(Map<String, Object> map);

    Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> getSchedulePageByCondition(String hoscode, String depcode, Integer pageNum, Integer pageSize);

    Schedule getSchelduelInfo(String scheduleId);

    ScheduleOrderVo getScheduleById(String scheduleId);

    Boolean updateAvailableNumber(String scheduleId, Integer availableNumber);

    void cannelSchedule(String scheduleId);
}
