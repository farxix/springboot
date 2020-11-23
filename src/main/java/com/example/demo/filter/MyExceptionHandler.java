package com.example.demo.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 注解验证角色和权限的话无法捕获异常，从而无法正确的返回给前端错误信息。
 * 所以加一个类用于拦截异常
 */
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    @ExceptionHandler
    @ResponseBody
    public String errorHandler(AuthorizationException e){
        log.error("没有通过权限验证",e);
        return "没有通过权限验证！";
    }
}
