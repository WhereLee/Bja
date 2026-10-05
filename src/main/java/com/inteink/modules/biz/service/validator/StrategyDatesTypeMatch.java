package com.inteink.modules.biz.service.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 校验strategyDates与strategyType的格式匹配性（按业务真实规则）
 */
@Target({ElementType.TYPE}) // 作用于类
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrategyDatesTypeMatchValidator.class)
public @interface StrategyDatesTypeMatch {
    // 精准匹配你的格式要求的错误提示
    String message() default "执行规则格式错误：1(每日)=HH:mm|2(每周)=1~7数字|3(每月)=1~31数字|4(指定日期)=YYYY-MM-DD HH:mm";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}