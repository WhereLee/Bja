package com.inteink.modules.biz.model.enums;

/**
 * 动作日志类型：1-手动 2-自动。
 */
public enum RodLogTypeEnum {
    MANUAL(1, "手动"),
    AUTO(2, "自动");

    private final Integer code;
    private final String desc;

    RodLogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
