package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 17:29
 * @Description: everything is ok
 */

public interface DepartmentService {
    void saveDepartment(Map<String, Object> map);

    Page getDepartmentPage(Map<String, Object> map);

    void remove(Map<String, Object> map);

    List<DepartmentVo> getDepartmentList(String hoscode);

    String getDepName(String hoscode, String depcode);


    Department getDepartment(String hoscode, String depcode);
}
