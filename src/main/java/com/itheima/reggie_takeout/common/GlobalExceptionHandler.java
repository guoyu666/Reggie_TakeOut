package com.itheima.reggie_takeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exeptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage()); // 报错记得打印日志
        if (ex.getMessage().contains("Duplicate entry")){
            // 获取已经存在的用户名，这里是从报错信息中获取
            String[] split = ex.getMessage().split(" ");    // 按照空格进行分割，最后生成的是字符串
            String msg = split[2] + "这个用户名已经存在";
            return R.error(msg);
        }
        return R.error("未知错误！");
    }
}
