package com.blllf.blogease.exception;

import com.blllf.blogease.pojo.Result;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器Spring Boot框架提供
 * @ControllerAdvice 注解是用来捕获Controller中抛出的指定类型的异常，从而实现不同类型的异常统一处理
 * */

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 对所用的异常进行捕获
     *  如果想要对某个特定异常进行更改 通过配置该异常类
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.error(StringUtils.hasLength(e.getMessage()) ? e.getMessage() : "操作失败");
    }
}
