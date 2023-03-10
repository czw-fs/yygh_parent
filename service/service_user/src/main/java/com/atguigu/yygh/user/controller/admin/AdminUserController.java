package com.atguigu.yygh.user.controller.admin;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/24 10:52
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/administrator/userinfo")
public class AdminUserController {

    @Autowired
    private UserInfoService userInfoService;

    //修改用户认证状态
    @PutMapping("/auth/{id}/{authStatus}")
    public R approval(@PathVariable Long id,
                      @PathVariable Integer authStatus){
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setAuthStatus(authStatus);

        userInfoService.updateById(userInfo);
        return R.ok();
    }

    //查看用户详细信息
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Map<String,Object> map = userInfoService.detail(id);
        return R.ok().data(map);
    }

    //修改用户操作
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable Long id,@PathVariable Integer status){
        userInfoService.updateStatus(id,status);
        return R.ok();
    }

    @GetMapping("/{pageNum}/{limit}")
    public R getUserInfoPage(@PathVariable Integer pageNum ,
                             @PathVariable Integer limit,
                             UserInfoQueryVo userInfoQueryVo){

        Page<UserInfo> userInfoPage = userInfoService.getUserInfoPage(pageNum,limit,userInfoQueryVo);
        return R.ok().data("total",userInfoPage.getTotal()).data("list",userInfoPage.getRecords());
    }

}
