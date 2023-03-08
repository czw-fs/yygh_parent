package com.atguigu.yygh.oss.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 * @author: fs
 * @date: 2023/2/22 17:51
 * @Description: everything is ok
 */
@Component
@ConfigurationProperties(prefix = "oss.file")
@PropertySource(value = {"classpath:oss.properties"})//不支持yml文件,不能和@EnableConfigurationProperties搭配使用
@Data
public class OssProperties {

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;
}
