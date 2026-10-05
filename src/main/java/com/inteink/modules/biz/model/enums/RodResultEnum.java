package com.inteink.modules.biz.model.enums;

/**
 * 动作结果：1-成功 2-失败。
 */
public enum RodResultEnum {
    SUCCESS(1, "成功"),
    FAIL(2, "失败");

    private final Integer code;
    private final String desc;

    RodResultEnum(Integer code, String desc) {
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
