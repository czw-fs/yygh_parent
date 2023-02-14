package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author: fs
 * @date: 2023/2/13 11:12
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/admin/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable("id")String id){
        Hospital hospital = hospitalService.detail(id);
        return R.ok().data("hospital",hospital);
    }

    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable("id")String id,@PathVariable("status") Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    @GetMapping("/{pageNum}/{size}")
    public R getHospitalPage(@PathVariable("pageNum")Integer pageNum, @PathVariable("size")Integer size, HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page = hospitalService.getHospitalPage(pageNum,size,hospitalQueryVo);
        //返回总记录数和当前页的数据
        return R.ok().data("total",page.getTotalElements()).data("list",page.getContent());
    }


}
