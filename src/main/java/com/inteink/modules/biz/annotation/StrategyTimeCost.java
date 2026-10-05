package com.inteink.modules.biz.annotation;

import java.lang.annotation.*;

/**
 * 方法耗时统计注解
 * 标注后AOP自动统计执行耗时，耗时>500ms自动打印WARN级慢接口告警
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyTimeCost {
    /** 慢接口阈值(ms)，默认500ms */
    long threshold() default 500;
}