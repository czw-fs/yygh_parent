package com.atguigu.yygh.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/8 19:07
 * @Description: everything is ok
 */
@Data
public class R {

    private Integer code;
    private Boolean success;
    private String message;
    private Map<String,Object> data = new HashMap<>();

    private R(){}

    public static R ok(){
        R r = new R();
        r.code = REnum.SUCCESS.code;
        r.success = REnum.SUCCESS.flag;
        r.message = REnum.SUCCESS.getMessage();
        return r;
    }

    public static R error(){
        R r = new R();
        r.code = REnum.ERROR.code;
        r.success = REnum.ERROR.flag;
        r.message = REnum.ERROR.getMessage();
        return r;
    }

    public R code(Integer code){
        this.code = code;
        return this;
    }

    public R success(Boolean success){
        this.success = success;
        return this;
    }

    public R message(String message){
        this.message = message;
        return this;
    }

    public R data(String key,Object object){
        this.data.put(key,object);
        return this;
    }

    public R data(Map<String,Object> map){
        this.data = map;
        return this;
    }
}
