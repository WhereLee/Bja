package com.inteink.modules.biz.annotation;

import java.lang.annotation.*;

/**
 * 入参/出参日志打印注解
 * 标注后AOP自动打印方法入参、出参，统一格式，生产环境可全局关闭
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyLogRecord {
}