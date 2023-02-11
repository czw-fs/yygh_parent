package com.atguigu.yygh.common.exception;

/**
 * @author: fs
 * @date: 2023/2/9 11:30
 * @Description: everything is ok
 */

//自定义异常必须是手动抛出!!!
public class YyghException extends RuntimeException{
    private Integer code;
    private String message;


    public YyghException(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
