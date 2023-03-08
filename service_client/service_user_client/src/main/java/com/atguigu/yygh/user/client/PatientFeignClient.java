package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author: fs
 * @date: 2023/3/5 18:05
 * @Description: everything is ok
 */
@FeignClient(value = "service-user")
public interface PatientFeignClient {

    @GetMapping("/user/userinfo/patient/{patientId}")
    public Patient getPatientById(@PathVariable("patientId") Long patientId);
}