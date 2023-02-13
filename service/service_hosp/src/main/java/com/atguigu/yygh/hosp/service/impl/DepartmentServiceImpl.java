package com.atguigu.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.hosp.repository.DepartmentRepository;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 17:29
 * @Description: everything is ok
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;



    @Override
    public void saveDepartment(Map<String, Object> map) {
        //将map转化为json字符串,再将json字符串转换为对应的对象
        String toJSONString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(toJSONString, Department.class);

        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();

        //根据医院编号和科室标号,精确定位,联合查询
        Department platformDepartment = departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);

        if(platformDepartment == null){
            //mongodb中没有该科室信息
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);

            departmentRepository.save(department);
        }else{
            //有,就修改(覆盖)
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);

            department.setId(platformDepartment.getId());

            departmentRepository.save(department);
        }

    }

    @Override
    public Page getDepartmentPage(Map<String, Object> map) {
        Integer page = (Integer)Integer.parseInt((String)map.get("page"));
        Integer limit = (Integer)Integer.parseInt((String)map.get("limit"));

        String hoscode = (String) map.get("hoscode");

        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> departmentExample = Example.of(department);
        //传过来的pageNum(page)是1,所以应该减一
        Pageable pageable = PageRequest.of(page - 1, limit);


        Page<Department> all = departmentRepository.findAll(departmentExample, pageable);
        return all;
    }

    @Override
    public void remove(Map<String, Object> map) {
        String hoscode = (String)map.get("hoscode");
        String depcode = (String)map.get("depcode");

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);

        if(department != null){
            departmentRepository.deleteById(department.getId());
        }
    }
}
