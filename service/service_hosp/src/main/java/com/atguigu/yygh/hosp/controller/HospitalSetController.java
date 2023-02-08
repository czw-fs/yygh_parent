package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-08
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "医院设置信息")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;




    @GetMapping("/findAll")
    @ApiOperation(value = "查询所有医院的设置信息")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("items",list);
    }

    //根据医院id删除医院设置信息
    @DeleteMapping("/deleteById")
    @ApiOperation(value = "根据医院id删除医院设置信息")
    public R removeById(@ApiParam(name = "id",value="医院设置id",required = true) String id){
        hospitalSetService.removeById(id);
        return R.ok();
    }
    /*
    @Api(tags=""):标记在接口类上
    @ApiOperation (value=""") :标记在方法上@ApiParam (value="") :标记在参数上
    @ApiModel(description="):对POJo类做说明
    @ApiModelProperty(value="):对PoJo类属性做说明
     */
}

