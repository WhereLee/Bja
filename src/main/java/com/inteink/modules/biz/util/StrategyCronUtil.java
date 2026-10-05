package com.inteink.modules.biz.util;

import com.inteink.common.exception.RRException;
import com.inteink.common.utils.StringUtils;
import com.inteink.modules.biz.model.entity.BizLiftingStrategy;
import com.inteink.modules.biz.model.enums.StrategyTypeEnum;

/**
 * 由策略类型 + 时间(HH:mm) 生成 Quartz cron。
 * 字段语义（Quartz）：秒 分 时 日 月 周 [年]。
 */
public final class StrategyCronUtil {

    private StrategyCronUtil() {
    }

    /**
     * @param strategy 策略（提供 type 与 dates）
     * @param hhmm     时间点 HH:mm（begin 或 end）
     * @return cron；hhmm 为空返回 null（表示该节点不生成）
     */
    public static String build(BizLiftingStrategy strategy, String hhmm) {
        if (StringUtils.isBlank(hhmm)) {
            return null;
        }
        int[] hm = parseTime(hhmm);
        int hour = hm[0];
        int minute = hm[1];
        Integer type = strategy.getStrategyType();
        String dates = strategy.getStrategyDates();
        StrategyTypeEnum typeEnum = type == null ? null : of(type);
        if (typeEnum == null) {
            throw new RRException("策略类型非法：" + type);
        }
        switch (typeEnum) {
            case DAILY:
                return "0 " + minute + " " + hour + " * * ?";
            case WEEKLY:
                if (StringUtils.isBlank(dates)) {
                    throw new RRException("每周策略需指定星期(如 2,3,5)");
                }
                return "0 " + minute + " " + hour + " ? * " + dates;
            case MONTHLY:
                if (StringUtils.isBlank(dates)) {
                    throw new RRException("每月策略需指定日期(如 1,15)");
                }
                return "0 " + minute + " " + hour + " " + dates + " * ?";
            case APPOINT:
                int[] ymd = parseDate(dates);
                // 指定年份，确保只触发一次
                return "0 " + minute + " " + hour + " " + ymd[1] + " " + ymd[2] + " ? " + ymd[0];
            default:
                throw new RRException("未支持的策略类型：" + typeEnum);
        }
    }

    private static StrategyTypeEnum of(Integer code) {
        for (StrategyTypeEnum e : StrategyTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    private static int[] parseTime(String hhmm) {
        String[] p = hhmm.trim().split(":");
        if (p.length != 2) {
            throw new RRException("时间格式应为 HH:mm，实际：" + hhmm);
        }
        try {
            int h = Integer.parseInt(p[0].trim());
            int m = Integer.parseInt(p[1].trim());
            if (h < 0 || h > 23 || m < 0 || m > 59) {
                throw new RRException("时间越界：" + hhmm);
            }
            return new int[]{h, m};
        } catch (NumberFormatException e) {
            throw new RRException("时间无法解析：" + hhmm);
        }
    }

    private static int[] parseDate(String date) {
        if (StringUtils.isBlank(date)) {
            throw new RRException("指定日期策略需填写日期 yyyy-MM-dd");
        }
        String[] p = date.trim().split("-");
        if (p.length != 3) {
            throw new RRException("日期格式应为 yyyy-MM-dd，实际：" + date);
        }
        try {
            return new int[]{Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])};
        } catch (NumberFormatException e) {
            throw new RRException("日期无法解析：" + date);
        }
    }
}
