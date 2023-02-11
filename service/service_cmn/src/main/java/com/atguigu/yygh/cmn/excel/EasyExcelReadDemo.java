package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;

/**
 * @author: fs
 * @date: 2023/2/10 16:47
 * @Description: everything is ok
 */
public class EasyExcelReadDemo {
//    public static void main(String[] args) {
//        EasyExcel.read("D:\\OneDrive\\桌面\\hello.xlsx", Studnent.class,new StudengListener()).sheet().doRead();
//    }

    public static void main(String[] args) {
        ExcelReader excelReader = EasyExcel.read("D:\\OneDrive\\桌面\\abc.xlsx").build();
        ReadSheet sheet1 = EasyExcel.readSheet(0).head(Studnent.class).registerReadListener(new StudengListener()).build();
        ReadSheet sheet2 = EasyExcel.readSheet(1).head(Studnent.class).registerReadListener(new StudengListener()).build();

        excelReader.read(sheet1,sheet2);

        excelReader.finish();
    }
}
