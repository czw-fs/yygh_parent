package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fs
 * @date: 2023/2/10 16:12
 * @Description: everything is ok
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Studnent {
    @ExcelProperty(value = "学生id")
    @ColumnWidth(value = 50)
    private Integer sid;
    private String name;
    private Integer age;
    private boolean gender;
}
