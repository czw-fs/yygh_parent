package com.atguigu.yygh.sms.service;

import com.atguigu.yygh.vo.msm.MsmVo;

/**
 * @author: fs
 * @date: 2023/2/21 15:39
 * @Description: everything is ok
 */
public interface SmsService {
    boolean sendCode(String phone);

    void sendMessage(MsmVo msmVo);
}
