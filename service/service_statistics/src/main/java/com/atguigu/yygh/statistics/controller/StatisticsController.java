package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.statistics.service.StatisticsService;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/countByData")
    public R statistics(OrderCountQueryVo orderCountQueryVo){
       Map<String,Object> map = statisticsService.statistics(orderCountQueryVo);
       return R.ok().data(map);
    }

}