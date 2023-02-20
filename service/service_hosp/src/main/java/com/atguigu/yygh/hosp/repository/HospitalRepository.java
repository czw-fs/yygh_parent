package com.atguigu.yygh.hosp.repository;

import com.atguigu.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/12 13:25
 * @Description: everything is ok
 */
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String name);
}
