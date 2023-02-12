package com.atguigu.yygh.hosp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author: fs
 * @date: 2023/2/12 13:17
 * @Description: everything is ok
 */
//将一个键对应多个键值对map转化为,一个键对应一个值的map
public class HttpRequestHelper {

    public static Map<String, Object> switchMap(Map<String, String[]> parameterMap) {
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();

        HashMap<String, Object> resultmap = new HashMap<>();

        for (Map.Entry<String, String[]> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue()[0];
            resultmap.put(key, value);
        }
        return resultmap;
    }
}
