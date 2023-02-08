package com.atguigu.yygh.common.result;

/**
 * @author: fs
 * @date: 2023/2/8 19:26
 * @Description: everything is ok
 */
public enum REnum {
    SUCCESS(20000,"成功",true),
    ERROR(20001,"失败",false)
    ;
    public Integer code;
    public String message;
    public Boolean flag;

    REnum(Integer code,String message, Boolean flag) {
        this.code = code;
        this.flag = flag;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }
}
