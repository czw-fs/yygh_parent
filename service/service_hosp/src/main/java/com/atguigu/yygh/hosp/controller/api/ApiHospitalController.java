package com.atguigu.yygh.hosp.controller.api;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.hosp.bean.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/12 12:56
 * @Description: everything is ok
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;


    @PostMapping("/hospital/show")
    public Result getHospitalInfo(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = HttpRequestHelper.switchMap(parameterMap);
        String hoscode =(String) map.get("hoscode");

        //sign验证验证:.......

        Hospital hospital = hospitalService.getHospitalByHosCode(hoscode);

        return Result.ok(hospital);
    }

    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){

        Map<String, Object> resultMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //第三方医院发来的sign
        String requestSignKey =(String) resultMap.get("sign");
        //第三方医院发来的hosCode
        String requestHosCode = (String)resultMap.get("hoscode");

        //根据第三方医院的hoscode去平台库中查找对应的sign,并与第三方医院中的sign对比
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHosCode);
        String encrypt = MD5.encrypt(platformSignKey);
        //第三方医院提供的sign不为空,平台查出来的sign不为空,且两值相等
        if(!StringUtils.isEmpty(requestSignKey) && !StringUtils.isEmpty(platformSignKey) && encrypt.equals(requestSignKey)){

            String logoData = (String)resultMap.get("logoData");
            String result = logoData.replaceAll(" ", "+");
            resultMap.put("logoData",result);

            hospitalService.save(resultMap);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存失败");
        }
    }
}
