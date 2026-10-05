package com.inteink.modules.biz.model.eums;

import lombok.Getter;

@Getter
public enum BizLiftingStrategyEnum {
    STRATEGY_STATUS_VALID(0L, "有效"),
    STRATEGY_STATUS_DELETED(1L, "逻辑删除"),
    STRATEGY_STATUS_PAUSED(2L, "暂停"),

    CHECK_STATE_PENDING(0, "待审核"),
    CHECK_STATE_PASS(1, "审核通过"),
    CHECK_STATE_REJECT(2, "审核驳回"),

    ACTION_LIFT(1, "升杆"),
    ACTION_LOWER(2, "降杆"),

    LOG_TYPE_ADD(1, "新增策略"),
    LOG_TYPE_UPDATE(2, "修改策略"),
    LOG_TYPE_AUDIT(3, "审核策略"),
    LOG_TYPE_PAUSE(4, "暂停策略"),
    LOG_TYPE_RESUME(5, "恢复策略"),
    LOG_TYPE_EXECUTE(6, "执行策略"),
    LOG_TYPE_REMOVE(7, "删除策略");

    private final Number code;
    private final String desc;

    BizLiftingStrategyEnum(Long code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    BizLiftingStrategyEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static BizLiftingStrategyEnum getByCode(Number code) {
        if (code == null) {
            return null;
        }
        for (BizLiftingStrategyEnum e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

    public static Integer getCheckStateCode(BizLiftingStrategyEnum e) {
        return e == null ? null : (Integer) e.getCode();
    }

    public static Long getStrategyStatusCode(BizLiftingStrategyEnum e) {
        return e == null ? null : (Long) e.getCode();
    }

    public static Integer getActionCode(BizLiftingStrategyEnum e) {
        return e == null ? null : (Integer) e.getCode();
    }

    public static Integer getLogTypeCode(BizLiftingStrategyEnum e) {
        return e == null ? null : (Integer) e.getCode();
    }

    public static String getCheckStateDesc(Integer code) {
        BizLiftingStrategyEnum e = getByCode(code);
        return (e != null && isCheckState(e)) ? e.getDesc() : "未知";
    }

    public static String getStrategyStatusDesc(Long code) {
        BizLiftingStrategyEnum e = getByCode(code);
        return (e != null && isStrategyStatus(e)) ? e.getDesc() : "未知";
    }

    public static String getActionDesc(Integer code) {
        if (code == null) return "未知";
        for (BizLiftingStrategyEnum item : values()) {
            if (isActionType(item) && code.equals(item.getCode())) {
                return item.getDesc();
            }
        }
        return "未知";
    }

    private static boolean isCheckState(BizLiftingStrategyEnum e) {
        return e == CHECK_STATE_PENDING || e == CHECK_STATE_PASS || e == CHECK_STATE_REJECT;
    }

    private static boolean isStrategyStatus(BizLiftingStrategyEnum e) {
        return e == STRATEGY_STATUS_VALID || e == STRATEGY_STATUS_DELETED || e == STRATEGY_STATUS_PAUSED;
    }

    private static boolean isActionType(BizLiftingStrategyEnum e) {
        return e == ACTION_LIFT || e == ACTION_LOWER;
    }

    // ========== 新增4个无参重载方法（核心优化，解决调用报错，提升优雅性） ==========
    public Long getStrategyStatusCode() {
        return (Long) this.getCode();
    }

    public Integer getCheckStateCode() {
        return (Integer) this.getCode();
    }

    public Integer getActionCode() {
        return (Integer) this.getCode();
    }

    public Integer getLogTypeCode() {
        return (Integer) this.getCode();
    }
}