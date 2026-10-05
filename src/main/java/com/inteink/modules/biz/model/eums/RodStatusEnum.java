package com.inteink.modules.biz.model.eums;

import lombok.Getter;

@Getter
public enum RodStatusEnum {
    VALID(0, "正常"),
    INVALID(1, "异常");

    private final Integer code;
    private final String desc;

    RodStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(Integer code) {
        for (RodStatusEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e.getDesc();
            }
        }
        return "异常";
    }
}