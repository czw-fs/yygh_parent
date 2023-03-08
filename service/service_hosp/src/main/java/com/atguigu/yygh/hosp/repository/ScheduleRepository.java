package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/12 21:41
 * @Description: everything is ok
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule findByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode, String hosScheduleId);

    Schedule findByHoscodeAndHosScheduleId(String hoscode,String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workdate);


    Schedule findByHosScheduleId(String scheduleId);
}
