package com.atguigu.yygh.oss.controller;

import com.atguigu.yygh.common.result.R;
import com.atguigu.yygh.oss.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: fs
 * @date: 2023/2/22 17:59
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/user/oss/file")
public class OssController {

    @Autowired
    private OssService ossService;

    @PostMapping("/upload")
    public R upload(MultipartFile file){
        System.out.println(file);
        String url = ossService.upload(file);
        System.out.println(url);
        return R.ok().data("url",url);
    }
}
