package com.atguigu.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: fs
 * @date: 2023/2/13 12:59
 * @Description: everything is ok
 */
//记得在方法前面加上被调用方Controller上的路径,不要只加方法上的路径
@FeignClient(value = "service-cmn")//被调用方在注册中心的名称
public interface DictFeignClient {

    /*
    记得在方法前面加上被调用方Controller上的路径,不要只加方法上的路径!!!!!!!!
    记得在方法前面加上被调用方Controller上的路径,不要只加方法上的路径!!!!!!!!
     */

    //根据医院所属的市区编号查询省市区的文字
    @GetMapping("/admin/cmn/{value}")
    String getNameByValue(@PathVariable("value") Long value);

    //根据医院的等级编号查询医院等级信息
    @GetMapping("/admin/cmn/{dictCode}/{value}")
    String getNameByDictCodeAndValue(@PathVariable("dictCode") String dictCode,
                                            @PathVariable("value") Long value);
}
