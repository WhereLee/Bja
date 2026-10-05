package com.inteink.modules.biz.model.enums;

/**
 * 策略操作日志类型：1-创建 2-修改 3-审核通过 4-审核驳回。
 */
public enum StrategyLogTypeEnum {
    CREATE(1, "创建"),
    UPDATE(2, "修改"),
    AUDIT_PASS(3, "审核通过"),
    AUDIT_REJECT(4, "审核驳回");

    private final Integer code;
    private final String desc;

    StrategyLogTypeEnum(Integer code, String desc) {
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
