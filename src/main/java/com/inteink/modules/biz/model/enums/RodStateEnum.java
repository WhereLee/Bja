package com.inteink.modules.biz.model.enums;

/**
 * 杆升降状态：0-默认(未动作) 1-升 2-降。
 */
public enum RodStateEnum {
    DEFAULT(0, "未动作"),
    UP(1, "升杆"),
    DOWN(2, "降杆");

    private final Integer code;
    private final String desc;

    RodStateEnum(Integer code, String desc) {
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
        for (RodStateEnum e : values()) {
            if (e.code.equals(code)) {
                return e.desc;
            }
        }
        return "";
    }
}
