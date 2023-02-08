package com.atguigu.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: fs
 * @date: 2023/2/8 14:52
 * @Description: everything is ok
 */
@SpringBootApplication
@ComponentScan("com.atguigu.yygh")//扫描依赖模块
@MapperScan(value = "com.atguigu.yygh.hosp.mapper")
public class ServiceHospMainStart {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStart.class,args);
    }
}
