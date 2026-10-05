package com.inteink.modules.biz.annotation;

import java.lang.annotation.*;

/**
 * 策略生命周期状态校验注解
 * 标注在【暂停/恢复/修改/删除/审核/执行】方法上，AOP自动校验操作合法性
 * 例：已删除策略禁止操作、待审核策略禁止暂停、未审核策略禁止执行
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StrategyLifeCycleCheck {
}