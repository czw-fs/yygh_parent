package com.atguigu.yygh.user.service.impl;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-21
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //获取用户输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //验证手机号和验证码非空
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new YyghException(20001,"手机号或验证码有误");
        }
        //对验证码做进一步确认
        String redisCode = (String)redisTemplate.opsForValue().get(phone);
        if(StringUtils.isEmpty(redisCode) || !redisCode.equals(code)){
            throw new YyghException(20001,"手机号或验证码有误");
        }


        //验证该手机号是否首次登录,是则注册当前用户信息
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("phone",phone);
        UserInfo userInfo = baseMapper.selectOne(userInfoQueryWrapper);

        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            baseMapper.insert(userInfo);
            userInfo.setStatus(1);
        }
        //验证用户的status
        if(userInfo.getStatus() == 0){
            throw new YyghException(20001,"用户锁定中");
        }
        //返回用户信息,设置初始化用户名
        HashMap<String, Object> result = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        result.put("name", name);

        String token = JwtHelper.createToken(userInfo.getId(), name);

        result.put("token", token);
        return result;
    }
}
