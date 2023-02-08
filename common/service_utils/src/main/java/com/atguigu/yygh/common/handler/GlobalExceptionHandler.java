package com.atguigu.yygh.common.handler;

import com.atguigu.yygh.common.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * @author: fs
 * @date: 2023/2/8 20:55
 * @Description: everything is ok
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)//粗粒度的异常处理
    public R handlerException(Exception ex){
        ex.printStackTrace();//输出异常:日志文件
        return R.error().message(ex.getMessage());
    }

    @ExceptionHandler(value = SQLException.class)//细粒度的异常处理
    public R handlerSqlException(SQLException sqlException){
        sqlException.printStackTrace();
        return R.ok().message("Sql异常");
    }

    @ExceptionHandler(value = ArithmeticException.class)//细粒度的异常处理
    public R handlerArithmeticException(ArithmeticException arithmeticException){
        arithmeticException.printStackTrace();
        return R.ok().message("数学异常");
    }

    @ExceptionHandler(value = RuntimeException.class)//粗粒度的异常处理
    public R handlerRuntimeException(RuntimeException runtimeException){
        runtimeException.printStackTrace();
        return R.ok().message("编译时异常");
    }
}
