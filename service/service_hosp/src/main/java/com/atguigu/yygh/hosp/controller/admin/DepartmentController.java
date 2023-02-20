package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/15 13:19
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public R getDepartmentList(@PathVariable("hoscode")String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",departmentVoList);
    }
}
