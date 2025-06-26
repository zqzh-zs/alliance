package com.neu.alliance.common.advice;

import com.neu.alliance.common.config.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ResponseBody
@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public Result handler(Exception e){
        log.error("发生异常,e: {}",e);
        return Result.fail("发生错误: "+e.getMessage());
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Result handler(NoResourceFoundException e){
        log.error("文件不存在,e: {}",e.getResourcePath());
        return Result.fail("发生错误，e: {}",e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public Result handler(HandlerMethodValidationException e){
        log.error("参数校验失败,e: {}",e.getMessage());
        return Result.fail("参数不合法");
    }


}
