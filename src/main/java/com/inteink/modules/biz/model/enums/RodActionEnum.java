package com.inteink.modules.biz.model.enums;

/**
 * 杆动作：1-升 2-降。
 */
public enum RodActionEnum {
    UP(1, "升杆"),
    DOWN(2, "降杆");

    private final Integer code;
    private final String desc;

    RodActionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isValid(Integer code) {
        for (RodActionEnum e : values()) {
            if (e.code.equals(code)) {
                return true;
            }
        }
        return false;
    }

    public static String descOf(Integer code) {
        for (RodActionEnum e : values()) {
            if (e.code.equals(code)) {
                return e.desc;
            }
        }
        return "";
    }
}
