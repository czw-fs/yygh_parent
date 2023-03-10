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
        //?????????pageNum??????!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
        1.list:????????????????????????????????????
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
                //??????pageNum?????????
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)
        );//????????????
        /*
        ???????????????Aggregation:??????????????????
        ??????????????????nputType:???????????????????????????????????????????????????????????????mongo????????????
        ???????????????outputType:??????????????????,????????????????????????
         */
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation1, Schedule.class, BookingScheduleRuleVo.class);
        //??????????????????????????????
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            //?????????:???????????????
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        /*
        2.total:??????????????????????????????
         */
        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate"));//????????????
        /*
        ???????????????Aggregation:??????????????????
        ??????????????????nputType:???????????????????????????????????????????????????????????????mongo????????????
        ???????????????outputType:??????????????????,????????????????????????
         */
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        Map<String,Object> scheduleHashMap = new HashMap<>();
        scheduleHashMap.put("list",mappedResults);
        scheduleHashMap.put("total",aggregate2.getMappedResults().size());

        //??????????????????
        Hospital hospital = hospitalService.getHospitalByHosCode(hoscode);
        //??????????????????
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        scheduleHashMap.put("baseMap",baseMap);

        return scheduleHashMap;
    }

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        //?????????workdate????????????????????????,?????????workdate?????????date??????
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
            throw new YyghException(20001,"????????????????????????");
        }
        BookingRule bookingRule = hospital.getBookingRule();

        IPage page = this.getListDate(pageNum,pageSize,bookingRule);

        //??????????????????????????????
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
                bookingScheduleRuleVo.setAvailableNumber(-1);//?????????????????????????????????????????????
                //bookingScheduleRuleVo.setStatus(0);
            }
            bookingScheduleRuleVo.setWorkDateMd(date);
            bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime()));

            bookingScheduleRuleVo.setStatus(0);

            //???????????????????????????????????????
            if(i == 0 && pageNum == 1){
                DateTime dateTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                //'??????????????????????????????????????????????????????????????????????????????:???????????????????????????????????????????????????
                if(dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }

            //?????????????????????????????????????????????
            if(pageNum == page.getPages() && i == (size - 1)){
                bookingScheduleRuleVo.setStatus(1);
            }

            bookingScheduleRuleVoArrayList.add(bookingScheduleRuleVo);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("list",bookingScheduleRuleVoArrayList);
        //??????????????????
        Map<String, String> baseMap = new HashMap<>();
        //????????????
        baseMap.put("hosname", hospitalService.getHospitalByHosCode(hoscode).getHosname());
        //??????
        Department department =departmentService.getDepartment(hoscode,depcode);
        //???????????????
        baseMap.put("bigname", department.getBigname());
        //????????????
        baseMap.put("depname", department.getDepname());
        //???
        baseMap.put("workDateString", new DateTime().toString("yyyy???MM???"));
        //????????????
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //????????????
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
        //???????????????????????????????????????????????????????????????????????????????????????????????????????????????: cycle+1
        String releaseTime = bookingRule.getReleaseTime();
        //??????????????????????????????????????????:
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        if(dateTime.isBeforeNow()){
            //??????????????????????????????,??????????????????
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

        //??????????????????
        if(end > list.size()){
            end = list.size();
        }

        //??????????????????????????????
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
     * ???Date?????????yyyy-MM-dd HH:mm????????????DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private void packageSchedule(Schedule schedule) {
        //??????????????????
        schedule.getParam().put("hosname",hospitalService.getHospitalByHosCode(schedule.getHoscode()).getHosname());
        //??????????????????
        schedule.getParam().put("depname", departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //????????????????????????
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "??????";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "??????";
            default:
                break;
        }
        return dayOfWeek;
    }
}
