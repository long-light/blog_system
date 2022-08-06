package com.longzx.blog.handler;

import com.longzx.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class AllExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result exHandler(Exception ex){
        ex.printStackTrace();
        return Result.fail(-100, "System Error");
    }
}
