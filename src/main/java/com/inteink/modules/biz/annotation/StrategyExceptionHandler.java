package com.inteink.modules.biz.annotation;

import java.lang.annotation.*;

/**
 * 全局异常统一捕获注解
 * 标注后AOP自动捕获所有异常，标准化日志打印+统一抛出，业务类无需写try-catch
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyExceptionHandler {
}