package com.inteink.modules.biz.model.enums;

/**
 * 策略类型：1-每日 2-每周 3-每月 4-指定日期(一次性)。
 */
public enum StrategyTypeEnum {
    DAILY(1, "每日"),
    WEEKLY(2, "每周"),
    MONTHLY(3, "每月"),
    APPOINT(4, "指定日期");

    private final Integer code;
    private final String desc;

    StrategyTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String descOf(Integer code) {
        for (StrategyTypeEnum e : values()) {
            if (e.code.equals(code)) {
                return e.desc;
            }
        }
        return "";
    }
}
