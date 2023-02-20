package com.atguigu.yygh.gateway.filter;

import com.google.gson.JsonObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author: fs
 * @date: 2023/2/14 15:41
 * @Description: everything is ok
 */
//@Component
public class MyGlobalFilter implements GlobalFilter , Ordered {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //登录接口的请求不拦截
        if(antPathMatcher.match("/admin/user/**",path)){
            System.out.println("登录:" + path);
           return chain.filter(exchange);
        }else {
            //非登录接口,要验证后通过
            List<String> strings = request.getHeaders().get("X-Token");
            if(strings == null){
                //拦截
                System.out.println("拦截:" + path);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://localhost:9528/");

                return response.setComplete();
            }else{
                System.out.println("放行:" + path);
                //放行
                return chain.filter(exchange);
            }
        }
    }

    //全局过滤器的执行顺序:值越小,优先级越高
    @Override
    public int getOrder() {
        return 0;
    }
}
