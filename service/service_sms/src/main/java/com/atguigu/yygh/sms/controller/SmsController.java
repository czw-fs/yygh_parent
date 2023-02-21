package com.atguigu.yygh.sms.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: fs
 * @date: 2023/2/21 15:39
 * @Description: everything is ok
 */
@RequestMapping ("/user/sms")
@RestController
public class SmsController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send/{phone}")
    public R sendCode(@PathVariable String phone){
        boolean flag = smsService.sendCode(phone);
        if(flag){
            return R.ok();
        }else {
            return R.error();
        }
    }

}
