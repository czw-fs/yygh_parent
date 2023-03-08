package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
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
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Map<String, Object> getSchedulePageByCondition(String hoscode, String depcode, Integer pageNum, Integer pageSize) {
        Hospital hospital = hospitalService.getHospitalByHosCode(hoscode);
        if(hospital == null){
            throw new YyghException(20001,"该医院信息不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();

        IPage page = this.getListDate(pageNum,pageSize,bookingRule);

        //当前页对应的时间列表
        List<Date> records = page.getRecords();


        //-------------------------------------------------------------------

        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode)
                .and("workDate").in(records);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);

        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();

        Map<Date, BookingScheduleRuleVo> collect =
                mappedResults.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));

        int size = records.size();

        ArrayList<BookingScheduleRuleVo> bookingScheduleRuleVoArrayList = new ArrayList<>();
        for(int i = 0;i< size;i++){
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if(bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                //bookingScheduleRuleVo.setWorkDateMd(date);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setReservedNumber(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);//当天所有医生的总的剩余可预约数
                //bookingScheduleRuleVo.setStatus(0);
            }
            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime()));

            bookingScheduleRuleVo.setStatus(0);

            //第一页第一条做特殊判断处理
            if(i == 0 && pageNum == 1){
                DateTime dateTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                //'如果医院规定的当前的挂号截止时间在此时此刻之前，说明:此时此刻已经过了当天的挂号截止时间
                if(dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            //最后欧一页的最后一条做特殊处理
            if(pageNum == page.getPages() && i == (size - 1)){
                bookingScheduleRuleVo.setStatus(1);
            }

            bookingScheduleRuleVoArrayList.add(bookingScheduleRuleVo);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("list",bookingScheduleRuleVoArrayList);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospitalService.getHospitalByHosCode(hoscode).getHosname());
        //科室
        Department department =departmentService.getDepartment(hoscode,depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());
        map.put("baseMap", baseMap);
        return map;
    }

    @Override
    public Schedule getSchelduelInfo(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleById(String scheduleId) {

        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);

        Hospital hospital = hospitalService.getHospitalByHosCode(schedule.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());

        Department department = departmentService.getDepartment(schedule.getHoscode(), schedule.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());

        Date date = new DateTime(schedule.getWorkDate()).plusDays(hospital.getBookingRule().getQuitDay()).toDate();
        DateTime dateTime = this.getDateTime(date, hospital.getBookingRule().getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toDate());

        Date workDate = schedule.getWorkDate();
        String stopTime = hospital.getBookingRule().getStopTime();
        Date toDate = this.getDateTime(workDate, stopTime).toDate();

        scheduleOrderVo.setStopTime(toDate);

        return scheduleOrderVo;
    }

    @Override
    public Boolean updateAvailableNumber(String scheduleId, Integer availableNumber) {

        Schedule schedule = scheduleRepository.findById(scheduleId).get();

        schedule.setAvailableNumber(availableNumber -1);
        schedule.setUpdateTime(new Date());

        scheduleRepository.save(schedule);
        return true;
    }

    @Override
    public void cannelSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findByHosScheduleId(scheduleId);
        if(schedule != null){
            schedule.setAvailableNumber(schedule.getAvailableNumber() + 1);
            scheduleRepository.save(schedule);
        }
    }

    private IPage getListDate(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        Integer cycle = bookingRule.getCycle();
        //此时此刻是否已经超过了医院规定的当天的挂号起始时间，如果此时此刻已经超过了: cycle+1
        String releaseTime = bookingRule.getReleaseTime();
        //今天医院规定的挂号的起始时间:
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        if(dateTime.isBeforeNow()){
            //超过了今天的预约时间,预约周期加一
            cycle = cycle +1;
        }

        ArrayList<Date> list = new ArrayList<>();
        for(int i = 0 ;i< cycle ;i++){
            DateTime dateTime1 = new DateTime().plusDays(i);
            String s = dateTime1.toString("yyyy-MM-dd");
            Date date = new DateTime(s).toDate();
            list.add(date);
        }

        int start = (pageNum - 1) * pageSize;
        int end = start +pageSize;

        //调整最后一页
        if(end > list.size()){
            end = list.size();
        }

        //当前页所有的时间数据
        ArrayList<Date> currentPageDateList = new ArrayList<>();
        for(int i = start ; i < end;i++){
            Date date = list.get(i);
            currentPageDateList.add(date);
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize, list.size());
        page.setRecords(currentPageDateList);
        return page;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
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
