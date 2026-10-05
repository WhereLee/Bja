package com.inteink.modules.biz.annotation;

import com.inteink.modules.biz.model.eums.BizLiftingStrategyEnum;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyOperLog {
    /** 日志类型（替换硬编码数字，直接绑定枚举，100%杜绝传参错误） */
    BizLiftingStrategyEnum logType();

    /** 日志操作描述（选填） */
    String value() default "";
}