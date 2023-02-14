package com.atguigu.yygh.hosp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author: fs
 * @date: 2023/2/8 14:52
 * @Description: everything is ok
 */
@SpringBootApplication
@ComponentScan("com.atguigu.yygh")//不仅能扫描自己还能扫描依赖模块
@MapperScan(value = "com.atguigu.yygh.hosp.mapper")
@EnableDiscoveryClient
//一定要指定扫描包,否则只会扫描当前模块,不会扫描依赖模块
@EnableFeignClients(basePackages = "com.atguigu.yygh")
public class ServiceHospMainStart {
    public static void main(String[] args) {
        SpringApplication.run(ServiceHospMainStart.class,args);
    }
}
