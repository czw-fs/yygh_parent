package com.atguigu.yygh.enums;

/**
 * @author: fs
 * @date: 2023/2/25 9:24
 * @Description: everything is ok
 */
public enum StatusEnum {

    LOCK(0,"锁定"),
    NORMAL(1,"正常");
    private Integer status;
    private String statusString;

    public static String getStatusStringByStatus(Integer status){
        for (StatusEnum value : StatusEnum.values()) {
            if(value.getStatus().intValue() == status.intValue()){
                return value.getStatusString();
            }
        }
        return "";
    }

    StatusEnum(Integer status, String statusString) {
        this.status = status;
        this.statusString = statusString;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "StatusEnum{" +
                "status=" + status +
                ", statusString='" + statusString + '\'' +
                '}';
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }
}
