package com.atguigu.yygh.order.service;

import java.util.Map;

/**
 * @author: fs
 * @date: 2023/3/7 15:01
 * @Description: everything is ok
 */
public interface WeiPayService {
    String createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    void paySuccess(Long orderId,Map<String,String> map);

    Boolean refund(Long orderId);
}
