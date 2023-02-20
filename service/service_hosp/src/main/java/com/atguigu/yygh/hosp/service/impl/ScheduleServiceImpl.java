package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

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

    @Override
    public Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode) {

        /*
        1.list:查询当前页符合条件的数据
         */
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation1 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                //记住pageNum要减一
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );//聚合条件
        /*
        第一个参数Aggregation:表示聚合条件
        第二个参数工nputType:表示输入类型，可以根据当前指定的字节码找到mongo对应集合
        第三个参数outputType:表示输出类型,封装聚合后的信息
         */
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation1, Schedule.class, BookingScheduleRuleVo.class);
        //当前页对应的列表数据
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            //工具类:转换为周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        /*
        2.total:求符合条件的总记录数
         */
        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate"));//聚合条件
        /*
        第一个参数Aggregation:表示聚合条件
        第二个参数工nputType:表示输入类型，可以根据当前指定的字节码找到mongo对应集合
        第三个参数outputType:表示输出类型,封装聚合后的信息
         */
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        Map<String,Object> scheduleHashMap = new HashMap<>();
        scheduleHashMap.put("list",mappedResults);
        scheduleHashMap.put("total",aggregate2.getMappedResults().size());

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHosCode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        scheduleHashMap.put("baseMap",baseMap);

        return scheduleHashMap;
    }

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        //这里的workdate严格区分数据类型,需要将workdate转化为date类型
        Date date = new DateTime(workdate).toDate();
        List<Schedule> scheduleList = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode, depcode, date);

        scheduleList.stream().forEach(item->{
                this.packageSchedule(item);
        });
        return scheduleList;
    }

    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalByHosCode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
