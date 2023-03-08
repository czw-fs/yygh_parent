package com.atguigu.yygh.order.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author: fs
 * @date: 2023/3/7 14:50
 * @Description: everything is ok
 */

//指定读取那个properties文件
@PropertySource(value = "classpath:weipay.properties")
//指定读取属性中公共的开头部分
@ConfigurationProperties(prefix = "weipay")
@Component
@Data
public class WeiPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;
}
