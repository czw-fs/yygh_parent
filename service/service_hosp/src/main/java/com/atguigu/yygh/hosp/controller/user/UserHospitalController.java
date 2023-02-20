package com.atguigu.yygh.hosp.controller.user;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/20 16:24
 * @Description: everything is ok
 */
@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation(value = "获取分页列表")
    @GetMapping("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(1, 1000000, hospitalQueryVo);

        return R.ok().data("list",hospitalPage.getContent());
    }

    @GetMapping("/{name}")
    public R findByName(@PathVariable String name){
        List<Hospital> list = hospitalService.findByNameLike(name);

        return R.ok().data("list",list);
    }

    @GetMapping("/detail/{hoscode}")
    public R getHospitalDetail(@PathVariable("hoscode")String hoscode){
        Hospital hospital = hospitalService.getHospitalDetail(hoscode);
        return R.ok().data("hospital",hospital);
    }
}
