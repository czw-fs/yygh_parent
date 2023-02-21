package com.atguigu.yygh.user.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: fs
 * @date: 2023/2/21 19:22
 * @Description: everything is ok
 */
@Data
@ConfigurationProperties(prefix = "weixin")
public class WeixinProperties {

    private String appid;
    private String appsecret;
    private String redirecturl;
}
