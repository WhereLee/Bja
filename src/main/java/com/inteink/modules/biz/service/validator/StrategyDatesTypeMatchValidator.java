package com.inteink.modules.biz.service.validator;

import com.inteink.modules.biz.model.dto.StrategyUpdateDTO;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * 校验器：严格匹配「类型1/4带时间，类型2/3仅数字」的规则
 */
public class StrategyDatesTypeMatchValidator implements ConstraintValidator<StrategyDatesTypeMatch, StrategyUpdateDTO> {
    // 各类型对应的格式正则（精准匹配你的要求）
    private static final Pattern DAILY_PATTERN = Pattern.compile("^\\d{2}:\\d{2}$"); // 每日：HH:mm（如21:00）
    private static final Pattern WEEKLY_PATTERN = Pattern.compile("^[1-7]$"); // 每周：1~7纯数字（如1、7）
    private static final Pattern MONTHLY_PATTERN = Pattern.compile("^([1-9]|[12]\\d|3[01])$"); // 每月：1~31纯数字（如5、31）
    private static final Pattern APPOINT_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$"); // 指定日期：完整格式

    @Override
    public boolean isValid(StrategyUpdateDTO dto, ConstraintValidatorContext context) {
        Integer strategyType = dto.getStrategyType();
        String strategyDates = dto.getStrategyDates();

        // 1. 若strategyType为空 或 strategyDates为空：无需校验（业务层处理必填逻辑）
        if (strategyType == null || StringUtils.isBlank(strategyDates)) {
            return true;
        }

        // 2. 按类型匹配精准格式
        switch (strategyType) {
            case 1: // 每日：仅时间
                return DAILY_PATTERN.matcher(strategyDates).matches();
            case 2: // 每周：仅1~7数字
                return WEEKLY_PATTERN.matcher(strategyDates).matches();
            case 3: // 每月：仅1~31数字
                return MONTHLY_PATTERN.matcher(strategyDates).matches();
            case 4: // 指定日期：完整日期时间
                return APPOINT_PATTERN.matcher(strategyDates).matches();
            default: // 类型不在1-4：由@Min/@Max校验，此处返回false
                return false;
        }
    }
}