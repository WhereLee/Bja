package com.inteink.modules.biz.exception;

import com.inteink.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;

/**
 * 升降策略模块专属异常处理器（仅作用于biz模块Controller，优先级高于框架全局处理器）
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.inteink.modules.biz.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StrategyExceptionHandler {

    // 加构造方法，验证是否被加载
    public StrategyExceptionHandler() {
        log.info("=====升降策略异常处理器初始化完成=====");
    }

    /**
     * 处理业务专属异常（StrategyBizException）
     */
    @ExceptionHandler(StrategyBizException.class)
    public Result handleStrategyBizException(StrategyBizException e) {
        log.error("升降策略业务异常：{}", e.getMsg(), e);
        return Result.error(e.getCode(), e.getMsg(), null);
    }

    /**
     * 处理参数校验异常（@Valid注解触发的MethodArgumentNotValidException）
     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        BindingResult bindingResult = e.getBindingResult();
//        // 拼接字段级错误提示
//        StringBuilder errorMsg = new StringBuilder();
//        for (FieldError fieldError : bindingResult.getFieldErrors()) {
//            errorMsg.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append("；");
//        }
//        String finalMsg = errorMsg.substring(0, errorMsg.length() - 1);
//        log.error("升降策略参数校验异常：{}", finalMsg, e);
//        return Result.error(400, finalMsg, null);
//    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        // 拼接字段级错误提示
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append("；");
        }
        // 关键修复：先判断字符串长度，避免截取索引为-1
        String finalMsg;
        if (errorMsg.length() > 0) {
            // 有错误信息时，截取最后一个分号前的内容
            finalMsg = errorMsg.substring(0, errorMsg.length() - 1);
        } else {
            // 无字段错误时，设置默认提示
            finalMsg = "请求参数格式错误";
        }
        log.error("升降策略参数校验异常：{}", finalMsg, e);
        return Result.error(400, finalMsg, null);
    }

    /**
     * 处理审核状态非法的自定义校验（兜底）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("升降策略参数非法：{}", e.getMessage(), e);
        return Result.error(400, e.getMessage(), null);
    }

    /**
     * 处理其他未捕获的异常（兜底，最终仍抛框架全局异常）
     */
    @ExceptionHandler(Exception.class)
    public Result handleOtherException(Exception e) {
        log.error("升降策略系统异常", e);
        // 非业务异常返回通用提示，避免暴露敏感信息
        return Result.error(500, "操作失败，请联系管理员", null);
    }
}