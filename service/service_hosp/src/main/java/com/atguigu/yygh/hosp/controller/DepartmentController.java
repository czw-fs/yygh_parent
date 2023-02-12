package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.hosp.bean.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 17:24
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/api/hosp")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/department/list")
    public Result<Page> list(HttpServletRequest request){

        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

        //验证:....

        Page page = departmentService.getDepartmentPage(map);

        return Result.ok(page);
    }

    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        //格式转换
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);

        departmentService.saveDepartment(map);
        return Result.ok();
    }

}
