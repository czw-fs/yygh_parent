package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 13:31
 * @Description: everything is ok
 */
public interface HospitalService {
    public void save(Map<String, Object> resultMap);

    String getSignKeyWithHoscode(String requestHosCode);

    Hospital getHospitalByHosCode(String hoscode);

    Page<Hospital> getHospitalPage(Integer pageNum, Integer size, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);


    Hospital detail(String id);
}
