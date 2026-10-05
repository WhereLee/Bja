package com.inteink.modules.biz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 耗时统计：标在方法上，由 TimeCostAspect 记录执行耗时；超过 warnMs 以 warn 提示。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeCost {

    /** 业务说明（便于日志辨认） */
    String value() default "";

    /** warn 阈值，单位毫秒，默认 1000 */
    long warnMs() default 1000L;
}
