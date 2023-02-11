package com.atguigu.yygh.cmn.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.CellData;

import java.util.Map;

/**
 * @author: fs
 * @date: 2023/2/10 16:48
 * @Description: everything is ok
 */
//不要加spring注解
public class StudengListener extends AnalysisEventListener<Studnent> {

    //解析excel中,每一行数据,封装到类中,该方法每执行一次,读取一行数据
    @Override
    public void invoke(Studnent studnent, AnalysisContext analysisContext) {
        System.out.println(studnent);
    }

    //解析excel中某个sheet的标题时调用
    @Override
    public void invokeHead(Map<Integer, CellData> headMap, AnalysisContext context) {
        System.out.println("标题为:"+headMap);
    }

    //excel解析完毕后,收尾方法,关闭连接
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
