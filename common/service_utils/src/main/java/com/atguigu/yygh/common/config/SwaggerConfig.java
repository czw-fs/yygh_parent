package com.atguigu.yygh.common.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: fs
 * @date: 2023/2/8 16:55
 * @Description: everything is ok
 */
@SpringBootConfiguration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket getAdminDocket(){
        return new Docket(DocumentationType.SWAGGER_2);
    }
}
