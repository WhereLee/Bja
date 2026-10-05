package com.inteink.modules.biz.model.eums;

import lombok.Getter;

/**
 * 升降杆状态枚举
 */
@Getter
public enum LiftingRodEnum {

    // 杆离线状态：0-在线、1-离线
    ROD_OFFLINE_ON(0, "在线"),
    ROD_OFFLINE_OFF(1, "离线"),

    // 杆运行状态：1-正常、2-异常
    ROD_STATUS_NORMAL(1, "正常"),
    ROD_STATUS_ABNORMAL(2, "异常");

    // 状态值
    private final Integer code;
    // 状态中文描述
    private final String desc;

    // 构造方法
    LiftingRodEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据杆离线状态值获取中文描述
     * @param code 离线状态值（0/1）
     * @return 中文描述，无匹配返回空字符串
     */
    public static String getRodOfflineDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (LiftingRodEnum e : values()) {
            if (e.getCode().equals(code) && e.name().startsWith("ROD_OFFLINE_")) {
                return e.getDesc();
            }
        }
        return "";
    }

    /**
     * 根据杆运行状态值获取中文描述
     * @param code 运行状态值（1/2）
     * @return 中文描述，无匹配返回空字符串
     */
    public static String getRodStatusDesc(Integer code) {
        if (code == null) {
            return "";
        }
        for (LiftingRodEnum e : values()) {
            if (e.getCode().equals(code) && e.name().startsWith("ROD_STATUS_")) {
                return e.getDesc();
            }
        }
        return "";
    }
}