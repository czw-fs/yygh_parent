package com.atguigu.yygh.common.handler;

import com.atguigu.yygh.common.exception.YyghException;
import com.atguigu.yygh.common.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @author: fs
 * @date: 2023/2/8 20:55
 * @Description: everything is ok
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)//粗粒度的异常处理
    public R handlerException(Exception ex){
        ex.printStackTrace();//输出异常:日志文件
        log.error(ex.getMessage());
        return R.error().message(ex.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)//细粒度的异常处理
    public R handlerSqlException(SQLException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message("Sql异常");
    }

    @ExceptionHandler(value = ArithmeticException.class)//细粒度的异常处理
    public R handlerArithmeticException(ArithmeticException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message("数学异常");
    }

    @ExceptionHandler(value = YyghException.class)//粗粒度的异常处理
    public R handlerRuntimeException(YyghException ex){
        ex.printStackTrace();
        log.error(ex.getMessage());
        return R.error().message(ex.getMessage()).code(ex.getCode());
    }
}
