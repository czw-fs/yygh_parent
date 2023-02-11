package com.atguigu.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.atguigu.yygh.cmn.excel.StudengListener;
import com.atguigu.yygh.cmn.listener.DictListener;
import com.atguigu.yygh.cmn.mapper.DictMapper;
import com.atguigu.yygh.cmn.service.DictService;

import com.atguigu.yygh.model.cmn.Dict;
import com.atguigu.yygh.vo.cmn.DictEeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-10
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {



    @Override
    @CacheEvict(value = "abc",allEntries = true)
    public void upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), DictEeVo.class,new DictListener(baseMapper)).sheet(0).doRead();
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> dicts = baseMapper.selectList(null);
        ArrayList<DictEeVo> dictEeVos = new ArrayList<>(dicts.size());

        for (Dict dict : dicts) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);//将第一个参数和第二个参数的属性名和类型一致,可将第一个参数的属性拷贝到第二个参数上
            dictEeVos.add(dictEeVo);
        }

        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("字典文件", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), DictEeVo.class)
                .sheet("学生列表一").doWrite(dictEeVos);
    }


    /*=============================================
         springcache:底层redis、memcache
           1. 导入starter依赖
           2. application.properties: redis连接信息
           3. 在配置类中提供一个cacheManager,在配置类上标记@EnableCaching开启缓存支持注解
           4.@Cacheable(value="")            ：key::value
               @Cacheable(value = "abc",keyGenerator = "keyGenerator")

      ============================================*/

    @Override
    @Cacheable(value = "abc",key = "'selectIndexList'+#pid")
    public List<Dict> getChildByListByPid(Long pid) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",pid);
        List<Dict> dicts = baseMapper.selectList(queryWrapper);
        for (Dict dict : dicts) {
            dict.setHasChildren(isHasChildren(dict.getId()));
        }
        return dicts;
    }

    private boolean isHasChildren(Long pid) {
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",pid);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count > 0;
    }


}
