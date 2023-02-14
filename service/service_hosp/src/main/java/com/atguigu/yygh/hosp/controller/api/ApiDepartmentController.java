package com.atguigu.yygh.hosp.controller.api;

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
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;


    //医院的删除
    @PostMapping("/department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());

        //验证:~~

        departmentService.remove(map);
        return Result.ok();
    }


    //查询科室信息
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
