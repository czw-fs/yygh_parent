package com.atguigu.yygh.hosp.controller.admin;

import com.atguigu.yygh.common.result.R;
import org.springframework.web.bind.annotation.*;

/**
 * @author: fs
 * @date: 2023/2/9 13:29
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @PostMapping("/login")
    public R login(){
            return R.ok().data("token","admin-token");
    }

    @GetMapping("/info")
    public R info(String taken){
        return R.ok().data("roles","[admin]")
                .data("introduction","I am a super administrator")
                .data("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif")
                .data("name","Super Admin");
    }
}
