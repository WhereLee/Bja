package com.inteink.modules.biz.model.enums;

/**
 * 策略审核状态：0-待审核 1-通过 2-驳回。
 */
public enum StrategyCheckStateEnum {
    PENDING(0, "待审核"),
    PASS(1, "审核通过"),
    REJECT(2, "审核驳回");

    private final Integer code;
    private final String desc;

    StrategyCheckStateEnum(Integer code, String desc) {
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
        for (StrategyCheckStateEnum e : values()) {
            if (e.code.equals(code)) {
                return e.desc;
            }
        }
        return "";
    }
}
