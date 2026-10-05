package com.inteink.modules.biz.annotation;

import com.inteink.modules.biz.model.enums.BizLogKind;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 业务日志注解：标在领域方法上，由 BizLogAspect 统一记录（操作人/结果/异常），
 * 业务代码不再内联写日志。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BizLog {

    /** 日志种类（选 handler） */
    BizLogKind kind();

    /** 细分操作：策略用 CREATE / UPDATE / AUDIT；杆动作用不到，留空 */
    String type() default "";
}
