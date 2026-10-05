package com.inteink.modules.biz.utils;

import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.entity.BizLiftingStrategyDetail;
import org.apache.commons.lang.StringUtils;

public class StrategyCronUtil {

    /**
     * 根据策略类型 + 时间明细，生成 Quartz Cron 表达式
     */
    public static String buildCron(BizLiftingStrategy strategy,
                                   BizLiftingStrategyDetail detail) {
        // 1. 空值校验（核心！避免NPE）
        if (strategy == null || detail == null) {
            throw new IllegalArgumentException("策略/策略明细参数不能为空");
        }
        String begin = detail.getDetailBegin(); // HH:mm
        if (StringUtils.isBlank(begin) || !begin.contains(":")) {
            throw new IllegalArgumentException("策略执行时间格式错误（需为HH:mm）：" + begin);
        }
        String strategyDates = strategy.getStrategyDates();
        // 每日策略无需strategyDates，其他类型必须有
        if (strategy.getStrategyType() != 1 && StringUtils.isBlank(strategyDates)) {
            throw new IllegalArgumentException("非每日策略的strategyDates不能为空");
        }

        // 2. 解析时分
        String[] parts = begin.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("时间格式错误，需为HH:mm（如09:37）：" + begin);
        }
        String hour = parts[0];
        String minute = parts[1];

        // 3. 生成Cron（原有逻辑保留）
        switch (strategy.getStrategyType()) {
            // 1.每日
            case 1:
                return String.format("0 %s %s * * ?", minute, hour);
            // 2.每周（strategyDates = "MON,WED,FRI"）
            case 2:
                return String.format("0 %s %s ? * %s", minute, hour, strategyDates);
            // 3.每月（strategyDates = "1,15,28"）
            case 3:
                return String.format("0 %s %s %s * ?", minute, hour, strategyDates);
            // 4.指定日期（strategyDates = "2025-12-31 07:00"）
            case 4:
                String[] dateTime = strategyDates.split(" ");
                if (dateTime.length != 2) {
                    throw new IllegalArgumentException("指定日期格式错误（需为YYYY-MM-DD HH:mm）：" + strategyDates);
                }
                String[] ymd = dateTime[0].split("-");
                if (ymd.length != 3) {
                    throw new IllegalArgumentException("日期格式错误（需为YYYY-MM-DD）：" + dateTime[0]);
                }
                String year = ymd[0];
                String month = ymd[1];
                String day = ymd[2];
                return String.format("0 %s %s %s %s ? %s", minute, hour, day, month, year);
            default:
                throw new IllegalArgumentException("不支持的策略类型：" + strategy.getStrategyType());
        }
    }
    public static String buildCron(BizLiftingStrategy strategy, BizLiftingStrategyDetail detail, boolean isEndTime) {
        // 1. 空值校验
        if (strategy == null || detail == null) {
            throw new IllegalArgumentException("策略/策略明细参数不能为空");
        }
        // 2. 选择时间字段（begin/end）
        String time = isEndTime ? detail.getDetailEnd() : detail.getDetailBegin();
        if (org.apache.commons.lang.StringUtils.isBlank(time) || !time.contains(":")) {
            return null; // 结束时间为空则返回null，不生成任务
        }
        // 3. 解析时分
        String[] parts = time.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("时间格式错误，需为HH:mm（如09:37）：" + time);
        }
        String hour = parts[0];
        String minute = parts[1];
        String strategyDates = strategy.getStrategyDates();

        // 4. 生成Cron（复用原有逻辑）
        switch (strategy.getStrategyType()) {
            case 1: // 每日
                return String.format("0 %s %s * * ?", minute, hour);
            case 2: // 每周
                if (org.apache.commons.lang.StringUtils.isBlank(strategyDates)) {
                    throw new IllegalArgumentException("每周策略的strategyDates不能为空");
                }
                return String.format("0 %s %s ? * %s", minute, hour, strategyDates);
            case 3: // 每月
                if (org.apache.commons.lang.StringUtils.isBlank(strategyDates)) {
                    throw new IllegalArgumentException("每月策略的strategyDates不能为空");
                }
                return String.format("0 %s %s %s * ?", minute, hour, strategyDates);
            case 4: // 指定日期（不支持结束时间）
                return null;
            default:
                throw new IllegalArgumentException("不支持的策略类型：" + strategy.getStrategyType());
        }
    }

}