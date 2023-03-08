package com.atguigu.yygh.user.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.prop.WeixinProperties;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
        //访问微信服务器,通过code获取access_token
        String result = HttpClientUtils.get(format);
        System.out.println("result = " + result);

        JSONObject jsonObject = JSONObject.parseObject(result);
        //access_token:访问微信服务器的一个凭证
        String access_token = jsonObject.getString("access_token");
        System.out.println("access_token = " + access_token);
        //openid:微信扫描用户在微信服务器的唯一标识
        String openid = jsonObject.getString("openid");
        System.out.println("openid = " + openid);

        //用openid去本地数据库中查找该用户
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("openid",openid);
        UserInfo userInfo = userInfoService.getOne(userInfoQueryWrapper);

        if(userInfo == null){
            //首次用微信登录,保存用户微信信息
            userInfo = new UserInfo();
            userInfo.setOpenid(openid);

            //给微信服务器发请求,获取当前用户信息
            StringBuilder sb = new StringBuilder();
            StringBuilder append1 = sb.append("https://api.weixin.qq.com/sns/userinfo")
                                        .append("?access_token=%s")
                                        .append("&openid=%s");
            String format1 = String.format(append1.toString(), access_token, openid);
            String s = HttpClientUtils.get(format1);
            JSONObject jsonObject1 = JSONObject.parseObject(s);
            //获取用户的微信昵称
            String nickname =(String) jsonObject1.get("nickname");

            userInfo.setNickName(nickname);
            userInfo.setStatus(1);

            userInfoService.save(userInfo);
        }


        //返回用户信息,设置初始化用户名
        HashMap<String, String> map = new HashMap<>();

        //不是首次登录
        //验证用户的status
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户锁定中");
        }

        //如果用户手机号为空,说明是首次微信登录,要求强制绑定手机号
        if(StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid",openid);
        }else {
            //如果用户手机号不为空,说明不是首次微信登录,不需要 绑定手机号
            map.put("openid","");
        }


        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        String token = JwtHelper.createToken(userInfo.getId(), name);

        map.put("token", token);

        return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
    }

}
