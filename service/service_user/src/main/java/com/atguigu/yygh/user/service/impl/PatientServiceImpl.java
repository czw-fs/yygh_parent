package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.client.DictFeignClient;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-24
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAll(String token) {
        Long userId = JwtHelper.getUserId(token);
        QueryWrapper<Patient> patientQueryWrapper = new QueryWrapper<>();
        patientQueryWrapper.eq("user_id",userId);

        List<Patient> patientList = baseMapper.selectList(patientQueryWrapper);
        patientList.stream().forEach(item->{
            this.packagePatient(item);
        });

        return patientList;
    }

    @Override
    public Patient detail(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packagePatient(patient);
        return patient;
    }

    @Override
    public List<Patient> selectList(QueryWrapper<Patient> patientQueryWrapper) {
        List<Patient> patients = baseMapper.selectList(patientQueryWrapper);
        patients.stream().forEach(item ->{
            this.packagePatient(item);
        });
        return patients;
    }

    private void packagePatient(Patient item) {
        String type = dictFeignClient.getNameByValue(Long.parseLong(item.getCertificatesType()));
        String province = dictFeignClient.getNameByValue(Long.parseLong(item.getProvinceCode()));
        String city = dictFeignClient.getNameByValue(Long.parseLong(item.getCityCode()));
        String district = dictFeignClient.getNameByValue(Long.parseLong(item.getDistrictCode()));

        item.getParam().put("certificatesTypeString",type);
        item.getParam().put("provinceString",province);
        item.getParam().put("cityString",city);
        item.getParam().put("districtString",district);
        item.getParam().put("fullAddress",province + city + district + item.getAddress());
    }


}
