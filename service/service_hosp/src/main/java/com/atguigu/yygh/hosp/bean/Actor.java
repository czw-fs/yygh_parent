package com.atguigu.yygh.hosp.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author: fs
 * @date: 2023/2/11 17:29
 * @Description: everything is ok
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "actor")//映射mongodb中对应的表名
public class Actor {
    @Id //当前属性和mongo集合中的主键是对应的
    private String abc;
    @Field(value = "actor_name")
    private String name;
    private Boolean gender;
    private Date birth;
}
