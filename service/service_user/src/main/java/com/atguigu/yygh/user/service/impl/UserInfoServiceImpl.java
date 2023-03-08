package com.atguigu.yygh.user.service.impl;


import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.JwtHelper;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.enums.StatusEnum;
import com.atguigu.yygh.model.acl.User;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.PatientMapper;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
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

    @Resource
    private PatientService patientService;

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

        String openid = loginVo.getOpenid();
        UserInfo userInfo = null;

        if(StringUtils.isEmpty(openid)){
            //验证该手机号是否首次登录,是则注册当前用户信息
            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.eq("phone",phone);
            userInfo = baseMapper.selectOne(userInfoQueryWrapper);
            if(userInfo == null){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                baseMapper.insert(userInfo);
                userInfo.setStatus(1);
            }
        }else {
            QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
            userInfoQueryWrapper.eq("openid",openid);
            userInfo = baseMapper.selectOne(userInfoQueryWrapper);

            //以前没有用手机号登录过,用微信号登录后,强制绑定手机号
            QueryWrapper<UserInfo> userInfoQueryWrapper1 = new QueryWrapper<>();
            userInfoQueryWrapper1.eq("phone",phone);
            UserInfo userInfo2 = baseMapper.selectOne(userInfoQueryWrapper1);

            if(userInfo2 == null){
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.updateById(userInfo);
            }else {
                //以前用手机号登录过,用微信号可以直接登录
                userInfo2.setOpenid(userInfo.getOpenid());
                userInfo2.setNickName(userInfo.getNickName());
                baseMapper.updateById(userInfo2);
                baseMapper.deleteById(userInfo.getId());
            }
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

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        System.out.println(AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        return userInfo;
    }

    @Override
    public Page<UserInfo> getUserInfoPage(Integer pageNum, Integer limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> userInfoPage = new Page<UserInfo>(pageNum,limit);

        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(userInfoQueryVo.getKeyword())){
            userInfoQueryWrapper.like("name",userInfoQueryVo.getKeyword())
                    .or().eq("phone",userInfoQueryVo.getKeyword());
        }
        if(!StringUtils.isEmpty(userInfoQueryVo.getStatus())){
            userInfoQueryWrapper.eq("status",userInfoQueryVo.getStatus());
        }
        if(!StringUtils.isEmpty(userInfoQueryVo.getAuthStatus())){
            userInfoQueryWrapper.eq("auth_status",userInfoQueryVo.getAuthStatus());
        }
        if(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeBegin())){
            userInfoQueryWrapper.ge("create_time",userInfoQueryVo.getCreateTimeBegin());
        }
        if(!StringUtils.isEmpty(userInfoQueryVo.getCreateTimeEnd())){
            userInfoQueryWrapper.le("create_time",userInfoQueryVo.getCreateTimeEnd());
        }

        Page<UserInfo> page = baseMapper.selectPage(userInfoPage, userInfoQueryWrapper);

        page.getRecords().stream().forEach(items ->{
            this.getPackageUserInfo(items);
        });
        return page;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if(status == 0 || status == 1){
            //mp支持直接修改
            UserInfo userInfo = new UserInfo();
            userInfo.setId(id);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> detail(Long id) {
        UserInfo userInfo = baseMapper.selectById(id);
        QueryWrapper<Patient> patientQueryWrapper = new QueryWrapper<>();
        patientQueryWrapper.eq("user_id",id);
        List<Patient> patientList = patientService.selectList(patientQueryWrapper);

        Map<String,Object> map = new HashMap<>();
        map.put("userInfo",userInfo);
        map.put("patients",patientList);
        return map;
    }

    private void getPackageUserInfo(UserInfo items) {
        Integer status = items.getStatus();
        Integer authStatus = items.getAuthStatus();

        items.getParam().put("statusString", StatusEnum.getStatusStringByStatus(status));
        items.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(authStatus));
    }
}
