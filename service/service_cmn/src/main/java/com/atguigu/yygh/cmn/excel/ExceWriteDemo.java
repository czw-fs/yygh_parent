package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import java.util.ArrayList;

/**
 * @author: fs
 * @date: 2023/2/10 16:09
 * @Description: everything is ok
 */
public class ExceWriteDemo {
    /*public static void main(String[] args) {



        EasyExcel.write("D:\\OneDrive\\桌面\\hello.xlsx", Studnent.class)
                .sheet("学生列表一").doWrite(studnents);
    }*/
    public static void main(String[] args) {

        ArrayList<Studnent> studnents1 = new ArrayList<>();
        studnents1.add(new Studnent(1,"张三",18,true));
        studnents1.add(new Studnent(2,"cba",18,false));
        ArrayList<Studnent> studnents2 = new ArrayList<>();
        studnents2.add(new Studnent(3,"abc",18,true));
        studnents2.add(new Studnent(4,"ccc",18,false));


        ExcelWriter excelWriter = EasyExcel.write("D:\\OneDrive\\桌面\\abc.xlsx", Studnent.class).build();
        WriteSheet sheet1 = EasyExcel.writerSheet(0, "学生列表1").build();
        WriteSheet sheet2 = EasyExcel.writerSheet(1, "学生列表2").build();

        excelWriter.write(studnents1,sheet1);
        excelWriter.write(studnents2,sheet2);

        excelWriter.finish();

    }


    }
