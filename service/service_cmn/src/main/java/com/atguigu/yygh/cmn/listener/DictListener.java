package com.atguigu.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;

/**
 * @author: fs
 * @date: 2023/2/11 12:54
 * @Description: everything is ok
 */
public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper){
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);

        QueryWrapper<Dict> dictEeVoQueryWrapper = new QueryWrapper<>();
        dictEeVoQueryWrapper.eq("id",dict.getId());
        Integer count = dictMapper.selectCount(dictEeVoQueryWrapper);

        if(count > 0){
            this.dictMapper.updateById(dict);
        }else{
            this.dictMapper.insert(dict);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
