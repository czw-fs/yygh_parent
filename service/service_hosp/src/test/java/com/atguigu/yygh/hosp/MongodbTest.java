package com.atguigu.yygh.hosp;

import com.atguigu.yygh.hosp.bean.Actor;

import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/11 17:27
 * @Description: everything is ok
 */
@SpringBootTest
public class MongodbTest {

    /*=============================================
         使用MongoTemplate三步：
           1.引入starter依赖
           2.配置mongodb连接信息
           3. 在使用的地方直接注入MongoTemplate对象
      ============================================*/

    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    public void test1(){
        mongoTemplate.insert(new Actor("113","六千",true,new Date()));
    }

    /*
    1.insert与save区别：insert只能做添加，save既可以做添加也可以做修改操作
    2.save做修改时，必须先查询，然后修改才行，不能直接修改。
    3.批量添加只能用insert
    */

    @Test
    public void test2(){
        Actor actor = mongoTemplate.findById("113", Actor.class);
        actor.setName("张三");
        mongoTemplate.save(actor);
    }

    @Test
    public void batchInsert(){

        ArrayList<Actor> actors = new ArrayList<>();
        actors.add(new Actor("11","a",true,new Date()));
        actors.add(new Actor("12","b",true,new Date()));
        actors.add(new Actor("13","c",true,new Date()));
        mongoTemplate.insert(actors, Actor.class);
    }

    //删除
    @Test
    public void test3(){
        //注意这里不要天字符串false,mongodb严格区分大小写和类型!!!!!!!
        Query query = new Query(Criteria.where("gender").is(false));
        DeleteResult remove = mongoTemplate.remove(query, Actor.class);
        System.out.println(remove.getDeletedCount());
    }

    //修改
    @Test
    public void test4(){
        Query query = new Query(Criteria.where("actor_name").is("hello world"));
        Update update = new Update();
        update.set("gender",false);
        update.set("birth",new Date());
        //库中有就改,没有就添加mongoTemplate.upsert()
        mongoTemplate.upsert(query,update,Actor.class);
    }

    //分页条件查询
    @Test
    public void test5(){
        int pageNum = 1;
        int size = 3;

        Query query = new Query(Criteria.where("gender").is(true));
        long total = mongoTemplate.count(query, Actor.class);
        List<Actor> actors = mongoTemplate.find(query.skip((pageNum - 1) * size).limit(size), Actor.class);

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("rows",actors);

        System.out.println(map);


    }
}
