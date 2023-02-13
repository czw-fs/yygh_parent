package com.atguigu.yygh.hosp.service;

import org.springframework.data.domain.Page;

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

}
