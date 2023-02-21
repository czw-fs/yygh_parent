package com.atguigu.yygh.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.prop.WeixinProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * @author: fs
 * @date: 2023/2/21 19:15
 * @Description: everything is ok
 */
@Controller
@RequestMapping("/user/userinfo/wx")
public class WeixinController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WeixinProperties weixinProperties;

    @RequestMapping("/param")
    @ResponseBody
    public R weixiLoginParam() throws UnsupportedEncodingException {

        String url = URLEncoder.encode(weixinProperties.getRedirecturl(), "UTF-8");

        HashMap<String, Object> map = new HashMap<>();
        map.put("appid",weixinProperties.getAppid());
        map.put("scope","snsapi_login");
        map.put("redirecturl",url);
        map.put("state",System.currentTimeMillis() + "");
        return R.ok().data(map);
    }


    @GetMapping("/callback")
    public String callback(String code,String state) throws Exception {
        StringBuffer append = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String format = String.format(append.toString(), weixinProperties.getAppid(), weixinProperties.getAppsecret(), code);

        String result = HttpClientUtils.get(format);
        System.out.println("result = " + result);

        JSONObject jsonObject = JSONObject.parseObject(result);

        String access_token = jsonObject.getString("access_token");
        System.out.println("access_token = " + access_token);
        String openid = jsonObject.getString("openid");
        System.out.println("openid = " + openid);

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("openid",openid);
        UserInfo userInfo = userInfoService.getOne(userInfoQueryWrapper);

        if(userInfo == null){
            //首次用微信登录,保存用户微信信息
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);
            userInfoService.save(userInfo);
        }

        return "";
    }

}
