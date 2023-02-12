package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 13:32
 * @Description: everything is ok
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Resource
    private HospitalSetMapper hospitalSetMapper;


    //插入
    @Override
    public void save(Map<String, Object> resultMap) {
        String resultJson = JSONObject.toJSONString(resultMap);
        //将指定JOSN字符串转化为对应对象
        Hospital hospital = JSONObject.parseObject(resultJson, Hospital.class);
        Hospital collection = hospitalRepository.findByHoscode(hospital.getHoscode());
        //不存在,就插入
        if(collection == null){
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else{
            //有,修改
            hospital.setStatus(collection.getStatus());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(collection.getIsDeleted());
            //传入id值,作为修改依据
            hospital.setId(collection.getId());

            hospitalRepository.save(hospital);
        }
    }

    //根据hoscode查询sign
    @Override
    public String getSignKeyWithHoscode(String requestHosCode) {
        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        hospitalSetQueryWrapper.eq("hoscode",requestHosCode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(hospitalSetQueryWrapper);

        if(hospitalSet == null){
            throw new YyghException(20001,"该医院信息不存在");
        }

        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getHospitalByHosCode(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        return hospital;
    }
}
