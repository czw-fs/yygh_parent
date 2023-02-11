package com.atguigu.yygh.cmn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.atguigu"})//扫描依项目赖模块包,如:common/service_utils
@MapperScan("com.atguigu.yygh.cmn.mapper")
public class ServiceCmnApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCmnApplication.class, args);
    }
}