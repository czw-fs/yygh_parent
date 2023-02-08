package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.service.HospitalSetService;

import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

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

    //锁定与解锁:更改状态
    @PutMapping("/status/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);

        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    //批量删除
    @DeleteMapping("/delete")
    public R batchDelete(List<Integer> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    //修改之回显数据
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id){
        return R.ok().data("item",hospitalSetService.getById(id));
    }
    //修改之修改数据
    @PutMapping("/update")
    public R update(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增接口")
    public R save(@RequestBody HospitalSet hospitalSet){
        //设置状态1:能使用,0:不能使用
        hospitalSet.setStatus(1);
        //当前时间戳+随机数+MD5加密
        Random random = new Random();
        String encrypt = MD5.encrypt(System.currentTimeMillis() + "" + random.nextInt(1000));
        hospitalSet.setSignKey(encrypt);

        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    @PostMapping("/page/{pageNum}/{size}")
    public R getPageInfo(@ApiParam(name = "pageNum",value = "当前页")@PathVariable("pageNum")Integer pageNum,
                         @ApiParam(name = "size",value = "每页显示多少条")@PathVariable("size")Integer size,
                         @RequestBody HospitalSetQueryVo hospitalSetQueryVo){

        Page<HospitalSet> page = new Page<>(pageNum,size);

        QueryWrapper<HospitalSet> hospitalSetQueryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
            hospitalSetQueryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
            hospitalSetQueryWrapper.like("hoscode",hospitalSetQueryVo.getHoscode());
        }

        hospitalSetService.page(page, hospitalSetQueryWrapper);

        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());

    }


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

