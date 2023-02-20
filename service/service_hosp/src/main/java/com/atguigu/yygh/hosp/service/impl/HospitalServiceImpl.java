package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.client.DictFeignClient;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.enums.DictEnum;
import com.atguigu.yygh.hosp.mapper.HospitalSetMapper;
import com.atguigu.yygh.hosp.repository.HospitalRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private DictFeignClient dictFeignClient;


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


    //平台查
    @Override
    public Page<Hospital> getHospitalPage(Integer pageNum, Integer size, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        if(!StringUtils.isEmpty(hospitalQueryVo.getHosname())){
            hospital.setHosname(hospitalQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getProvinceCode())){
            hospital.setProvinceCode(hospitalQueryVo.getProvinceCode());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getCityCode())){
            hospital.setCityCode(hospitalQueryVo.getCityCode());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getHostype())){
            hospital.setHostype(hospitalQueryVo.getHostype());
        }
        if(!StringUtils.isEmpty(hospitalQueryVo.getDistrictCode())){
            hospital.setDistrictCode(hospitalQueryVo.getDistrictCode());
        }
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                //.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<Hospital> hospitalExample = Example.of(hospital,matcher);
        /*
        * pageNum一定要减一!!!!!!!!!!!!!!!!!!!!!!!!
        */
        PageRequest pageRequest = PageRequest.of(pageNum - 1, size, Sort.by("createTime").ascending());

        Page<Hospital> page = hospitalRepository.findAll(hospitalExample, pageRequest);

        page.getContent().stream().forEach(item->{
            packageHospital(item);
        });

        return page;
    }
    //根据各种码获取省市地区名称
    public void packageHospital(Hospital item){
        String provinceCode = item.getProvinceCode();
        String cityCode = item.getCityCode();
        String districtCode = item.getDistrictCode();
        String hostype = item.getHostype();

        String provinceName = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityName = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtName = dictFeignClient.getNameByValue(Long.parseLong(districtCode));
        String level = dictFeignClient.getNameByDictCodeAndValue(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hostype));

        item.getParam().put("hostypeString",level);
        item.getParam().put("fullAddress",provinceName + cityName + districtName + item.getAddress());
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if(status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital detail(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.packageHospital(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> findByNameLike(String name) {
        return hospitalRepository.findByHosnameLike(name);
    }

    @Override
    public Hospital getHospitalDetail(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        this.packageHospital(hospital);
        return hospital;
    }
}
